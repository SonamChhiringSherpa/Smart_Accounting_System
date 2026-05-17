document.addEventListener("DOMContentLoaded", function() {
	var eyeOpenIcon = '<svg class="icon-eye" viewBox="0 0 24 24" aria-hidden="true"><path d="M2 12s3.5-6 10-6 10 6 10 6-3.5 6-10 6S2 12 2 12z"></path><circle cx="12" cy="12" r="3"></circle></svg>';
	var eyeOffIcon = '<svg class="icon-eye-off" viewBox="0 0 24 24" aria-hidden="true"><path d="M3 3l18 18"></path><path d="M10.6 10.6A2 2 0 0 0 12 14a2 2 0 0 0 1.4-.6"></path><path d="M7.1 7.1C3.9 8.8 2 12 2 12s3.5 6 10 6c1.7 0 3.2-.4 4.4-1"></path><path d="M14.1 6.3C19.2 7.2 22 12 22 12s-.9 1.6-2.6 3.1"></path></svg>';

	function setPasswordToggleState(button, input, isVisible) {
		input.type = isVisible ? "text" : "password";
		button.classList.toggle("is-visible", isVisible);
		button.innerHTML = isVisible ? eyeOpenIcon : eyeOffIcon;
		var label = isVisible ? "Hide password" : "Show password";
		button.setAttribute("aria-label", label);
		button.setAttribute("title", label);
		button.setAttribute("aria-pressed", String(isVisible));
	}

	document.querySelectorAll("[data-password-toggle]").forEach(function(button) {
		var control = button.closest(".password-control");
		var input = control ? control.querySelector("input") : null;

		if (!input) {
			return;
		}

		setPasswordToggleState(button, input, input.type === "text");

		button.addEventListener("click", function() {
			setPasswordToggleState(button, input, input.type === "password");
		});
	});

	var resendButton = document.getElementById("resendButton");
	var expiryTimer = document.getElementById("expiryTimer");

	if (resendButton) {
		var wait = Number(resendButton.getAttribute("data-wait"));

		function updateResendButton() {
			if (wait > 0) {
				resendButton.disabled = true;
				resendButton.textContent = "Resend OTP in " + wait + "s";
				wait--;
			} else {
				resendButton.disabled = false;
				resendButton.textContent = "Resend OTP";
				clearInterval(resendInterval);
			}
		}

		var resendInterval = setInterval(updateResendButton, 1000);
		updateResendButton();
	}

	if (expiryTimer) {
		var expiry = Number(expiryTimer.textContent);

		setInterval(function() {
			if (expiry > 0) {
				expiry--;
				expiryTimer.textContent = expiry;
			}
		}, 1000);
	}
});
