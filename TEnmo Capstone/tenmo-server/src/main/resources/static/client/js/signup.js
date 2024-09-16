document.addEventListener("DOMContentLoaded", () => {
    const signupForm = document.querySelector("form");

    signupForm.addEventListener("submit", async (event) => {
        event.preventDefault(); 
        const username = document.querySelector("#username").value;
        const password = document.querySelector("#password").value;

        try {
            const response = await fetch("/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }),
            });

            if (response.ok) {
                
                window.location.href = "/client/login.html";
            } else {
                alert("Registration failed. Please try a different username.");
            }
        } catch (error) {
            console.error("Error during registration:", error);
        }
    });
});
