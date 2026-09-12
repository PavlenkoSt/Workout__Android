# Google Play lifetime unlock

RepStation uses Google Play Billing 9.1.0 directly. There is no RevenueCat SDK,
API key, initialization, entitlement lookup, or remotely hosted paywall.
The custom Compose paywall lives in `ui/PaywallHostActivity.kt` and uses the app theme.

## Product

- App ID: `com.stanislav_pav.repstation`
- One-time product ID: `com.stanislav_pav.repstation.pro` (confirmed by the developer)
- Build version code: `6`
- The product must have an active, available **buy** purchase option in Google Play Console.
- Use the regular lifetime purchase option; this paywall excludes rentals, preorders, and promotional offers.
- Prices and currency formatting come from Google Play. The app never hardcodes the sale price.
- Purchases are acknowledged and **never consumed**, so they remain restorable.

The default product ID can be overridden with the Gradle property
`PLAY_PRO_PRODUCT_ID`. No RevenueCat configuration is needed.

## Access and restoration

Pro removes the limits of 3 presets, 5 goals, and 10 records, and unlocks training statistics.
Data is not deleted when access changes. Limits apply even when Google Play is unavailable.

One application-scoped billing client handles purchases across activities. On resume,
the app queries Google Play ownership, handles completed pending purchases, and retries
acknowledgement. Restore purchases explicitly queries the same account's owned products.
A successful query with no matching completed purchase removes cached paid access.
A failed query keeps the last known access; a price lookup failure cannot block restoration.

Paid access is cached for offline use. This cache is excluded from backup/device transfer,
so a new installation must first restore from Google Play while online. Refund/revoke
changes take effect when Google Play's owned-purchases query reflects them; there is no
backend or real-time refund notification processor. A refund alone may not revoke the item;
use **refund and revoke** when testing removal of ownership.

This is a client-only integration: it trusts the Play Billing response and does not perform
independent server-side purchase-token verification. Local checks/cache are not tamper-proof.
For stronger fraud protection, a backend can later verify tokens with the Play Developer API.

## Private access code

`PRO_UNLOCK_CODE` remains an optional Gradle property. Existing device unlocks in
`monetization_prefs/local_pro_unlocked` are preserved. The access-code button is shown
only when a nonblank code is configured. Invalid codes do not remove paid access.
This code is embedded in the APK and is for private/friends access, not secure licensing.

## Test through Google Play

1. Confirm the existing one-time product and its buy option are active in your tester's country.
2. Build/sign the release using your existing upload key in Android Studio. This repository
   does not configure release signing. Upload version code 6 (or a higher unused code) to an
   internal test track and add your Google account as a tester and a **license tester**.
3. Install from the internal-track opt-in link using that Google Play account. A sideloaded
   debug build is suitable for UI inspection, but an internal-track build is the reliable
   end-to-end test path. License testers can use Google's test payment methods.
4. Open Unlock Pro. Check the currency/price, scroll through the benefits, and close/reopen it.
5. Cancel checkout: remain free, then reopen and purchase with a test payment method.
6. Successful purchase: see “Pro is yours”, return to the app, and check all four Pro features.
7. Force-stop/reopen, then launch offline: previously purchased access should remain available.
8. Clear app data or reinstall, reconnect, and restore with the same Google account.
9. Test a slow/pending payment: remain free until approval, then resume/reopen to unlock.
10. Refund **and revoke** the test purchase; once Google Play reflects the change, resume
    the app and verify the free limits return without deleting saved data.
11. Test offline/unavailable billing while free: show a retryable error and never grant Pro.

Acknowledgement must complete within Google's three-day window. If confirmation fails,
the app keeps the completed purchase accessible and shows a retry message. Resume/retry
while online to finish; without a backend, a user who never returns cannot be retried remotely.

## Retiring RevenueCat

The developer confirmed there are no existing paying customers, so no paid-entitlement
migration is required. The previous paywall was RevenueCat's `Paywall` component, not a
custom purchase screen. Its remote design is replaced by the native screen in this project.

After testing and rolling out the new build, the RevenueCat project can be retired from its
dashboard. Old installed versions still depend on that project; remove them from test tracks
or ensure users update before deleting it. Keep the Google Play app and product active.
No remote RevenueCat or Google Play resources were deleted by this code change.

## Local verification

```sh
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug
./gradlew :app:connectedDebugAndroidTest
```

The second command needs an emulator/device. UI tests do not make purchases.
The debug APK is `app/build/outputs/apk/debug/app-debug.apk`.

Official references:
- https://developer.android.com/google/play/billing/integrate
- https://developer.android.com/google/play/billing/test
- https://developer.android.com/google/play/billing/security
