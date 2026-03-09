## Key flow

1. Request externalRequest = ElevatorSystem.handleExternalRequest(floor, direction)
2. Elevator e1 = Scheduler.assignExternalRequest(externalRequest)
3. e1.addRequest(externalRequest)
4. e1.addRequest(internalRequest) // to destination floor - cabin request - floor button clicked inside elevator