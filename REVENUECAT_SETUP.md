# RevenueCat Pro Setup

This app is wired for a free app with a one-time Pro unlock.

## App constants

- RevenueCat entitlement id: `pro`
- Suggested Google Play one-time product id: `workout_pro_lifetime`
- Free limits are defined in `app/src/main/java/com/stanislav_pav/repstation/monetization/MonetizationConfig.kt`

## Local configuration

Add the Android public SDK key from RevenueCat to `~/.gradle/gradle.properties` or this project's `gradle.properties`:

```properties
REVENUECAT_API_KEY=goog_xxxxxxxxxxxxxxxxx
```

Do not commit a private/service-account key. RevenueCat SDK keys are public client keys, but keeping environment-specific values out of git still makes release setup cleaner.

## RevenueCat dashboard

1. Create a RevenueCat project and add an Android app.
2. Use the final Play Store package name before importing products.
3. Create entitlement `pro`.
4. In Google Play Console, create a one-time product, for example `workout_pro_lifetime`.
5. Connect Google Play to RevenueCat and import the product.
6. Attach `workout_pro_lifetime` to entitlement `pro`.
7. Create an offering and add the product as a lifetime package.
8. Mark that offering as current.

The app reads `offerings.current.availablePackages` and buys the first package.

## Current Pro gates

- Presets: free users can create up to 3.
- Goals: free users can create up to 5.
- Records: free users can create up to 10.
- Training statistics button opens the Pro screen for free users.

Existing local data is never deleted when a user is free. Gates only block creating more items or opening Pro-only features.
