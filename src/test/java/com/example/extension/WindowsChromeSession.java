package com.example.extension;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.SECURITY_ATTRIBUTES;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.manager.SeleniumManager;
import org.openqa.selenium.manager.SeleniumManagerOutput.Result;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.ExcessiveImports", "PMD.TooManyMethods"})
final class WindowsChromeSession implements AutoCloseable {

    private static final int DESKTOP_ALL_ACCESS = 0x01FF;
    private static final int HTTP_OK = 200;
    private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration SHUTDOWN_TIMEOUT = Duration.ofSeconds(5);

    private final RemoteWebDriver webDriver;
    private final HANDLE driverProcess;
    private final HANDLE desktop;
    private boolean closed;

    private WindowsChromeSession(RemoteWebDriver webDriver, HANDLE driverProcess, HANDLE desktop) {
        this.webDriver = webDriver;
        this.driverProcess = driverProcess;
        this.desktop = desktop;
    }

    static boolean isSupported() {
        return isSupported(System.getProperty("os.name"));
    }

    static boolean isSupported(String operatingSystemName) {
        return operatingSystemName != null && operatingSystemName.startsWith("Windows");
    }

    static WindowsChromeSession start(ChromeOptions chromeOptions) {
        HANDLE desktop = null;
        HANDLE driverProcess = null;
        boolean started = false;
        try {
            String desktopName = "selenium-" + UUID.randomUUID();
            desktop = createDesktop(desktopName);
            int port = findAvailablePort();
            driverProcess = startChromeDriver(resolveChromeDriver(), desktopName, port);
            waitUntilReady(port, driverProcess);

            RemoteWebDriver webDriver = new RemoteWebDriver(
                    URI.create("http://127.0.0.1:" + port).toURL(),
                    chromeOptions);
            log.info("started ChromeDriver on dedicated Windows desktop {}", desktopName);
            started = true;
            return new WindowsChromeSession(webDriver, driverProcess, desktop);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not start ChromeDriver on a dedicated Windows desktop", exception);
        } finally {
            if (!started) {
                cleanupFailedStart(driverProcess, desktop);
            }
        }
    }

    RemoteWebDriver driver() {
        return webDriver;
    }

    static String buildDriverCommandLine(Path driverExecutable, int port) {
        return quoteArgument(driverExecutable.toString()) + " --port=" + port;
    }

    private static Path resolveChromeDriver() {
        Result result = SeleniumManager.getInstance().getBinaryPaths(List.of("--browser", "chrome"));
        if (result.getCode() != 0 || result.getDriverPath() == null || result.getDriverPath().isBlank()) {
            throw new IllegalStateException("Selenium Manager could not resolve ChromeDriver: " + result.getMessage());
        }
        return Path.of(result.getDriverPath());
    }

    private static HANDLE createDesktop(String desktopName) {
        HANDLE desktop = DesktopApi.INSTANCE.CreateDesktop(
                desktopName,
                null,
                null,
                0,
                DESKTOP_ALL_ACCESS,
                null);
        if (desktop == null) {
            throw new IllegalStateException("Could not create a Windows desktop; error " + Native.getLastError());
        }
        return desktop;
    }

    private static int findAvailablePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            return serverSocket.getLocalPort();
        }
    }

    private static HANDLE startChromeDriver(Path driverExecutable, String desktopName, int port) {
        Path driverDirectory = Objects.requireNonNull(
                driverExecutable.getParent(),
                "ChromeDriver executable must have a parent directory");
        STARTUPINFO startupInformation = new STARTUPINFO();
        startupInformation.cb = new DWORD(startupInformation.size());
        startupInformation.lpDesktop = "WinSta0\\" + desktopName;
        startupInformation.dwFlags = WinBase.STARTF_USESHOWWINDOW;
        startupInformation.wShowWindow = new WORD(WinUser.SW_HIDE);

        PROCESS_INFORMATION processInformation = new PROCESS_INFORMATION();
        boolean started = Kernel32.INSTANCE.CreateProcess(
                driverExecutable.toString(),
                buildDriverCommandLine(driverExecutable, port),
                null,
                null,
                false,
                new DWORD(0),
                null,
                driverDirectory.toString(),
                startupInformation,
                processInformation);
        if (!started) {
            throw new IllegalStateException("Could not start ChromeDriver; Windows error " + Native.getLastError());
        }
        Kernel32.INSTANCE.CloseHandle(processInformation.hThread);
        return processInformation.hProcess;
    }

    private static void waitUntilReady(int port, HANDLE driverProcess) {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/status"))
                .timeout(Duration.ofSeconds(1))
                .GET()
                .build();
        long deadline = System.nanoTime() + STARTUP_TIMEOUT.toNanos();
        try (HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build()) {
            while (System.nanoTime() < deadline) {
                if (Kernel32.INSTANCE.WaitForSingleObject(driverProcess, 0) == WinBase.WAIT_OBJECT_0) {
                    throw new IllegalStateException("ChromeDriver exited before accepting connections");
                }
                if (isReady(httpClient, request)) {
                    return;
                }
                sleepBeforeRetry();
            }
        }
        throw new IllegalStateException("ChromeDriver did not accept connections within " + STARTUP_TIMEOUT);
    }

    private static boolean isReady(HttpClient httpClient, HttpRequest request) {
        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == HTTP_OK;
        } catch (IOException exception) {
            log.trace("waiting for ChromeDriver", exception);
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for ChromeDriver", exception);
        }
    }

    private static void sleepBeforeRetry() {
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for ChromeDriver", exception);
        }
    }

    private static String quoteArgument(String argument) {
        return argument.chars().anyMatch(Character::isWhitespace) ? '"' + argument + '"' : argument;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            webDriver.quit();
        } finally {
            stopProcess(driverProcess);
            closeDesktop(desktop);
        }
    }

    private static void cleanupFailedStart(HANDLE driverProcess, HANDLE desktop) {
        stopProcess(driverProcess);
        closeDesktop(desktop);
    }

    private static void stopProcess(HANDLE process) {
        if (process == null) {
            return;
        }
        try {
            int waitResult = Kernel32.INSTANCE.WaitForSingleObject(process, (int) SHUTDOWN_TIMEOUT.toMillis());
            if (waitResult == WinError.WAIT_TIMEOUT) {
                Kernel32.INSTANCE.TerminateProcess(process, 0);
                Kernel32.INSTANCE.WaitForSingleObject(process, (int) SHUTDOWN_TIMEOUT.toMillis());
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(process);
        }
    }

    private static void closeDesktop(HANDLE desktop) {
        boolean closeFailed = desktop != null && !DesktopApi.INSTANCE.CloseDesktop(desktop);
        if (closeFailed && log.isWarnEnabled()) {
            log.warn("could not close dedicated Windows desktop; error {}", Native.getLastError());
        }
    }

    @SuppressWarnings("PMD.MethodNamingConventions")
    private interface DesktopApi extends StdCallLibrary {
        DesktopApi INSTANCE = Native.load("user32", DesktopApi.class, W32APIOptions.UNICODE_OPTIONS);

        HANDLE CreateDesktop(
                             String desktopName,
                             String device,
                             Pointer deviceMode,
                             int flags,
                             int desiredAccess,
                             SECURITY_ATTRIBUTES securityAttributes);

        boolean CloseDesktop(HANDLE desktop);
    }
}
