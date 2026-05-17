document.addEventListener("DOMContentLoaded", function() {
	var rows = document.querySelectorAll("[data-transaction-row]");
	var searchInput = document.querySelector("[data-table-search]");
	var typeFilter = document.querySelector("[data-type-filter]");

	function applyTableFilters() {
		var searchValue = searchInput ? searchInput.value.toLowerCase() : "";
		var typeValue = typeFilter ? typeFilter.value : "all";

		rows.forEach(function(row) {
			var matchesSearch = row.textContent.toLowerCase().indexOf(searchValue) !== -1;
			var matchesType = typeValue === "all" || row.getAttribute("data-type") === typeValue;
			row.style.display = matchesSearch && matchesType ? "" : "none";
		});
	}

	if (searchInput) {
		searchInput.addEventListener("input", applyTableFilters);
	}

	if (typeFilter) {
		typeFilter.addEventListener("change", applyTableFilters);
	}

	document.querySelectorAll("[data-bar-value]").forEach(function(bar) {
		var value = Number(bar.getAttribute("data-bar-value"));
		bar.style.width = Math.min(Math.max(value, 0), 100) + "%";
	});
});
