# 💳 Payment Gateway – Low Level Design (LLD)

---

# 1️⃣ Requirements

## Functional Requirements

| Category        | Requirement |
|-----------------|------------|
| Core Ops        | Initiate payment |
| Core Ops        | Process payment |
| Core Ops        | Validate payment details |
| Core Ops        | Return payment status |
| Payment Methods | Support Card payments |
| Payment Methods | Support UPI |
| Payment Methods | Support NetBanking |
| Payment Methods | Support Wallet |
| Lifecycle       | Handle success / failure |
| Lifecycle       | Handle timeout |
| Lifecycle       | Retry failed payments |
| Refunds         | Initiate refund |
| Refunds         | Partial refund support |
| Webhooks        | Notify merchant of payment status |
| Security        | Prevent duplicate transactions |
| Security        | Idempotent payment processing |

---

## Non-Functional Requirements

| Category     | Requirement |
|--------------|------------|
| Performance  | Low-latency payment processing |
| Scalability  | Handle high TPS (1000+ per second) |
| Concurrency  | Multiple concurrent payment requests |
| Security     | PCI compliance, encryption |
| Reliability  | No double charging |
| Availability | 99.99% uptime |
| Consistency  | Strong consistency for transaction state |

---

# 2️⃣ Core Entities

| Entity                | Responsibility |
|-----------------------|---------------|
| PaymentGateway        | Orchestrates payment flow |
| Payment               | Represents transaction |
| PaymentRequest        | Incoming merchant request |
| PaymentMethod         | Abstract payment type |
| CardPayment           | Card-specific logic |
| UPIPayment            | UPI-specific logic |
| NetBankingPayment     | NetBanking logic |
| PaymentProcessor      | Executes payment via bank |
| Refund                | Handles refund operations |
| TransactionRepository | Stores payment state |
| IdempotencyManager    | Prevent duplicate processing |
| WebhookService        | Sends async notifications |

---

## Key Relationships

### Inheritance
- `PaymentMethod` → `CardPayment`
- `PaymentMethod` → `UPIPayment`
- `PaymentMethod` → `NetBankingPayment`

### Composition
- `Payment` contains `PaymentMethod`
- `PaymentGateway` uses `PaymentProcessor`
- `Payment` has `TransactionStatus`

### Aggregation
- `PaymentGateway` uses `TransactionRepository`

---

# 3️⃣ Design Patterns – Must Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Strategy | Different payment methods | Card / UPI / Wallet | Extensible payment handling |
| Factory | Create payment method | PaymentMethodFactory | Avoid if-else explosion |
| State | Payment lifecycle | INITIATED → SUCCESS → FAILED → REFUNDED | Clear state transitions |
| Adapter | Integrate external bank APIs | RazorpayAdapter, StripeAdapter | Decouple external APIs |
| Singleton | PaymentGateway instance | Global orchestration | Central coordination |

---

# 4️⃣ Design Patterns – Good to Have

| Design Pattern | Used For | Example | Benefit |
|----------------|----------|----------|----------|
| Chain of Responsibility | Validation pipeline | FraudCheck → LimitCheck → BalanceCheck | Modular validation |
| Observer | Webhook notifications | MerchantListener | Decoupled event notification |
| Command | Payment execution | ProcessPaymentCommand | Retry & audit logging |
| Decorator | Add logging/metrics | LoggingPaymentProcessor | Non-intrusive monitoring |

---

# 5️⃣ Key Design Decisions

### 1. Strategy for Payment Methods
Different payment types have different:
- Validation logic
- Processing steps
- Retry mechanisms

Strategy isolates this cleanly.

### 2. State Pattern for Payment Lifecycle
Prevents invalid transitions:
- Cannot refund before success
- Cannot process twice

### 3. Idempotency Key Handling
- Merchant sends idempotency key
- Store key with transaction
- Reject duplicate requests

### 4. Strong Consistency
Payment state updates must be:
- Atomic
- Transactionally stored

### 5. Important Invariants
- No double charging
- Payment state transitions must be valid
- Refund amount ≤ payment amount
- Idempotency must guarantee single execution

---

# 6️⃣ Concurrency Handling

## What Can Go Wrong
- Duplicate payment requests
- Race condition during refund
- Double charging due to retries
- Concurrent webhook + status update

## Locking Strategy

### Transaction-Level Locking
- Lock on `paymentId`
- Use DB row-level locking

### Idempotency Control
- Unique constraint on idempotency key

### Optimistic Locking
- Version field in Payment entity

---

## Deadlock Prevention
- Lock single payment record at a time
- Avoid nested locks across transactions

---

## Atomic Operations

Process Payment:
1. Validate idempotency
2. Create transaction record
3. Lock transaction
4. Process via processor
5. Update state
6. Release lock
7. Send webhook

---

# 7️⃣ Common Interview Extensions

- Add recurring payments
- Add subscription billing
- Add fraud detection engine
- Add split payments
- Add escrow support
- Add chargeback handling
- Add rate limiting per merchant
- Add distributed payment processing
- Add circuit breaker for bank failures

---

# 8️⃣ Where Interviewers Try to Push

- How do you prevent double charging?
- How do you ensure idempotency?
- What happens if webhook fails?
- How do you handle bank timeout?
- How do you scale to 10K TPS?
- How to make this distributed?
- How to handle partial failure?
- How to support eventual consistency?
- How to make system PCI compliant?
- What if payment succeeds but DB update fails?

---