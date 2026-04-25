import { build, context } from "esbuild";
import { mkdir } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const projectRoot = path.resolve(__dirname, "..");
const outputFile = path.join(projectRoot, "build/resources/main/static/javascript/bundle.js");
const watchMode = process.argv.includes("--watch");
const mode = process.env.NODE_ENV ?? (watchMode ? "development" : "production");

const options = {
    absWorkingDir: projectRoot,
    bundle: true,
    define: {
        "process.env.NODE_ENV": JSON.stringify(mode)
    },
    entryPoints: ["src/main/resources/static/javascript/index.jsx"],
    jsx: "automatic",
    loader: {
        ".css": "css",
        ".eot": "file",
        ".gif": "file",
        ".jpeg": "file",
        ".jpg": "file",
        ".png": "file",
        ".svg": "dataurl",
        ".ttf": "dataurl",
        ".woff": "dataurl",
        ".woff2": "dataurl"
    },
    logLevel: "info",
    minify: mode === "production",
    outfile: outputFile,
    platform: "browser",
    sourcemap: true,
    target: ["es2020"]
};

await mkdir(path.dirname(outputFile), { recursive: true });

if (watchMode) {
    const watcher = await context(options);
    await watcher.watch();

    const shutdown = async () => {
        await watcher.dispose();
        process.exit(0);
    };

    process.on("SIGINT", shutdown);
    process.on("SIGTERM", shutdown);
    console.log(`Watching frontend assets in ${mode} mode...`);
} else {
    await build(options);
}
