# Design the Windows Settings App

## Problem Characteristics
> Policy-aware, observable, command-driven configuration management system

Similar problems
1. Design an OS-Level Settings and Configuration Engine
2. Design a Centralized Configuration Management System for Cloud Infrastructure
3. Design a Declarative Configuration Control Plane for k8s
4. Design a User and Admin Controlled Mobile Settings Framework

## Thought Process

1. Settings are CRUD - incorrect
2. Settings are stateful objects with rules - correct
3. Settings aren't key-value; they have behaviour.
4. Changing settings impacts other stuffs -> changes must be observable -> observer design pattern + Manager (mediator design pattern)
5. Who can change the settings? -> Policy aware -> User<Admin<Org
6. What if change goes wrong -> undo (command design pattern) -> changes are commands
7. What is the worst mistake that can happen -> configuration is a control plane

## Keywords
Module, OS API wrapper (facade pattern), reliability>scalability, 

## Design Patterns
<img width="497" height="392" alt="Screenshot 2026-01-31 at 2 20 19 AM" src="https://github.com/user-attachments/assets/3dfc8562-28c6-48af-ba68-906d21991af3" />
