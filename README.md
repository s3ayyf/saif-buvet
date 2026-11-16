# Saif Mini Markeet

Android app for a small shop: goods with their cartons and barcodes, sales, profit or loss on every item, what is made at home (fricassé), credit sales, shop and personal expenses.

The whole app is one page, `app/src/main/assets/index.html`, shown in a WebView. It works offline and keeps its data on the phone. The camera barcode reader is the only native part.

This branch (`saif-mini-markeet`) is a separate app from Saif Buvet on `main`: its own package name, so both can be installed on the same phone. Every push to this branch builds the APK with GitHub Actions and publishes it on the Releases page as `saif-mini-markeet.apk` under the `mini-markeet` tag.
