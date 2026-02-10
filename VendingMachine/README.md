# Vending Machine – Low Level Design (LLD)

---

## 1. Requirements

### a. Functional Requirements

| Category | Questions to Ask |
|--------|------------------|
| Items | What items are sold? (snacks, drinks, combos) |
| Inventory Model | Fixed slots or dynamic inventory? |
| Pricing | Fixed price per item or configurable pricing? |
| Item Selection | Single item per transaction or multiple? |
| Payment Methods | Coins, notes, card, UPI, wallet? |
| Change Handling | Should machine return exact change? |
| Out of Stock | How to handle item unavailability? |
| Refunds | When and how refunds are issued? |
| Dispensing | One item at a time or batch dispense? |

---

### b. Non-Functional Requirements

| Category | Questions / Constraints |
|--------|--------------------------|
| Concurrency | Can multiple users interact simultaneously? |
| Performance | Acceptable time for dispense and change return? |
| Consistency | Inventory correctness under concurrent access |
| Reliability | What if payment succeeds but dispense fails? |
| Extensibility | Easy addition of new items/payment methods |
| Fault Tolerance | Power failure or machine restart handling |
| Observability | Low-stock alerts, error monitoring |

---

## 2. Core Domain Entities

| Entity | Responsibility |
|------|----------------|
| VendingMachine | Orchestrates overall workflow |
| Inventory | Tracks item quantities |
| Slot | Physical container for an item |
| Item | Represents product (id, name, price) |
| Transaction | Tracks current purchase |
| PaymentService | Handles payment processing |
| DispenseService | Dispenses selected item |
| ChangeDispenser | Calculates and returns change |

---

## 3. Core System Flows

| Flow | Steps |
|----|-------|
| Primary Flow | Select item → Insert money → Validate payment → Dispense item → Return change |
| Out of Stock Flow | Select item → Detect unavailable → Reject selection / refund |
| Payment Failure | Insert money → Payment fails → Refund |
| Dispense Failure | Payment success → Dispense fails → Retry or refund |

---

## 4. Design Patterns – Must Have

| Design Pattern | Used For | Example | Why It Is Needed |
|---------------|---------|---------|-----------------|
| State | Machine behavior | IDLE, HAS_MONEY, DISPENSING | Prevents invalid actions |
| Strategy | Payment handling | CoinPayment, UPIPayment | Add payment types easily |
| Factory | Object creation | PaymentFactory | Avoids if-else logic |
| Singleton | Machine control | VendingMachine instance | Single control unit |

---

## 5. Design Patterns – Good to Have

| Design Pattern | Used For | Example | Benefit |
|---------------|---------|---------|---------|
| Template Method | Purchase flow | Validate → Pay → Dispense | Enforces workflow |
| Observer | Inventory alerts | Low-stock notifications | Monitoring & alerts |
| Command | User actions | InsertCoinCommand | Retry / undo support |
| Adapter | External payments | Card / UPI adapters | Integrates external systems |

---

## 6. Concurrency & Data Consistency

| Area | Strategy |
|----|---------|
| Slot access | Lock per slot (slot-level locking) |
| Inventory update | Atomic decrement |
| Dispense operation | Idempotent dispense |
| Payment handling | External, async-safe |
| State transitions | Atomic state changes |

---

## 7. Edge Cases & Failure Handling

| Scenario | Expected Behavior |
|--------|-------------------|
| Item out of stock | Reject selection |
| Payment success, dispense failure | Retry or refund |
| Power failure mid-transaction | Refund on restart |
| Duplicate input | Ignore or idempotent handling |
| Insufficient change | Block transaction |

---

## 8. Explicitly Out of Scope

- UI / display rendering  
- Persistence layer / database design  
- Remote monitoring dashboards  
- Analytics and reporting  

---

## 9. One-Line Interview Summary

> “I designed the vending machine as a state-driven system using State and Strategy patterns, with a template-based purchase flow, slot-level locking for concurrency, and robust failure handling.”
