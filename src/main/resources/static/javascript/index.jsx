import { createRoot } from "react-dom/client";
import App from "./app";

const rootElement = document.getElementById("content");

if (!rootElement) {
    throw new Error("Missing #content mount element.");
}

createRoot(rootElement).render(<App />);
