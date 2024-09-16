document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.querySelector("form");

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const username = document.querySelector("#username").value;
        const password = document.querySelector("#password").value;

        try {
            const response = await fetch("/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }),
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token);
                window.location.href = "/client/main.html";
            } else {
                alert("Login failed. Please check your username and password.");
            }
        } catch (error) {
            console.error("Error during login:", error);
        }
    });
});
