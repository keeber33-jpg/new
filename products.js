// Product catalog data is loaded from the existing Glass-bottles catalog.
// This compatibility file keeps the new site working while the catalog is migrated.
if (!window.PRODUCTS) {
  const script = document.createElement('script');
  script.src = 'https://keeber33-jpg.github.io/Glass-bottles/products.js';
  script.async = true;
  document.head.appendChild(script);
}
