# Parking Lot – Low Level Design (LLD)

## 1. Requirements

### Functional Requirements

| Category | Questions to Ask |
|--------|------------------|
| Parking Scope | Single parking lot or multiple locations? |
| Vehicle Types | Bike, Car, SUV, Truck, EV? |
| Parking Spot Types | Compact, Large, Handicapped, EV? |
| Entry / Exit Gates | Single gate or multiple concurrent gates? |
| Ticketing System | Paper ticket, QR code, RFID? |
| Pricing Model | Flat rate or time-based? Vehicle-based pricing? |
| Payment Methods | Cash, Card, UPI, Online payments? |
| Additional Features | Availability display, reservation, lost ticket handling? |

---

### Non-Functional Requirements

| Category | Questions / Constraints |
|--------|--------------------------|
| Scalability | Max concurrent vehicles (100 vs 10,000+)? |
| Performance | Expected latency at entry and exit gates? |
| Consistency | Is overbooking allowed? (Usually No) |
| Concurrency | Multiple entry/exit gates working in parallel? |
| Extensibility | Will new vehicle, spot, or pricing types be added? |
| Reliability | Handling failures at payment or gate level? |

---

## 2. Core Entities

| Entity | Responsibility |
|------|----------------|
| ParkingLot | Maintains global state, floors, gates, pricing |
| ParkingFloor | Contains parking spots grouped by type |
| ParkingSpot | Represents an individual parking space |
| Vehicle | Holds vehicle-specific information |
| ParkingTicket | Tracks entry time, exit time, vehicle, and spot |
| EntryGate | Issues parking tickets |
| ExitGate | Calculates fee and processes payment |
| PaymentService | Integrates with external payment systems |

---

## 3. Design Patterns – Must Have

| Design Pattern | Used For | Example | Why It Is Needed |
|---------------|---------|---------|-----------------|
| Strategy | Pricing logic | HourlyPricing, FlatRatePricing | Pricing rules change frequently |
| Factory | Object creation | VehicleFactory, ParkingSpotFactory | Avoids if-else, improves extensibility |
| Singleton | Global state | ParkingLot instance | Single source of truth |
| Observer | Notifications | Spot updates to DisplayBoard | Decouples UI from core logic |

---

## 4. Design Patterns – Good to Have

| Design Pattern | Used For | Example | Benefit |
|---------------|---------|---------|---------|
| Chain of Responsibility | Spot allocation | Compact → Large → Overflow | Flexible allocation flow |
| State | Ticket lifecycle | ACTIVE, PAID, LOST | Avoids boolean flags |
| Command | Gate operations | IssueTicketCommand | Auditing and retry support |
| Adapter | Payment integration | RazorpayAdapter, PaytmAdapter | Easy gateway replacement |
| Template Method | Entry / Exit flow | Standardized gate workflow | Enforces flow, allows customization |


---