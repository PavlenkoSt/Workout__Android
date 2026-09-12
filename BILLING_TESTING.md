# Direct billing test strategy

```yaml
test_strategy:
  artifact: "Lifetime purchases and custom paywall"
  rationale: "Replacing the purchase provider changes paid access and checkout."
  criticality: "HIGH"
  selected_types:
    - rationale: "Exercise ownership transitions and acknowledgement failures without charging users."
      type: "unit"
      size: "small"
      framework: "JUnit"
      dependencies: ["In-memory store gateway and entitlement cache"]
      gate: "Gate 1"
    - rationale: "Verify checkout controls and error/pending states in the actual Compose UI."
      type: "component"
      size: "medium"
      framework: "Compose UI test"
      dependencies: ["Android emulator"]
      gate: "Gate 3"
    - rationale: "Google Play owns the purchase service; real restoration and payment require a licensed tester."
      type: "integration"
      size: "large"
      framework: "Manual Google Play license testing"
      dependencies: ["Internal-track build", "Play product", "License tester"]
      gate: "Gate 2"
  rejected_types:
    - reason: "No independently deployed application API."
      type: "contract"
    - reason: "Finite ownership states are covered with explicit transition cases."
      type: "property-based"
    - reason: "No deployment pipeline in this repository."
      type: "smoke"
  deliberately_skipped:
    - why: "The developer must exercise their Play account and product; local tests cannot establish real purchase ownership."
      what: "Automated real-money checkout"
```

## Test cases to cover

- [unit] Completed lifetime purchase grants access and acknowledges once.
- [unit] Already acknowledged purchase restores without acknowledging again.
- [unit] Pending or unrelated products never grant Pro.
- [unit] Failed ownership query preserves cached access; successful empty query removes paid access.
- [unit] Local access-code unlock survives an empty purchase query and invalid code attempts.
- [unit] Acknowledgement failure preserves completed ownership and is retried on refresh.
- [unit] Failure to load the price cannot prevent ownership restoration.
- [unit] Cancelled checkout clears progress without revoking access.
- [unit] Changed price requires reviewing the new price before checkout.
- [unit] Duplicate purchase callbacks do not acknowledge twice.
- [component] Buy is unavailable without a price and during pending payment.
- [component] Localized price is displayed on checkout; restore and close remain available.
- [component] Billing errors expose a retry action.
- [integration] Buy, cancel, pending completion, process restart, reinstall/restore, offline access, and refund/revoke using Play license testing.
- [unit] An already-owned response still restores access while a price refresh is in progress.

## Migration scope

The developer confirmed the product ID is `com.stanislav_pav.repstation.pro` and that
there are no existing paying customers. Migration of historical RevenueCat purchases
is therefore not required.

## Verification result

- All 17 purchase-manager tests pass (18 unit tests including the existing example test).
- Debug APK and Android UI-test APK assemble successfully.
- Android lint completes with no errors; unrelated existing warnings remain.
- The four Compose UI tests compile but could not execute locally: both attempts to boot
  the temporary Android emulator failed with hypervisor/memory-protection errors.
- Real Google Play checkout remains for the developer's license-tester account. Follow
  `GOOGLE_PLAY_BILLING_SETUP.md` for buy, pending, restoration, and refund/revoke checks.
