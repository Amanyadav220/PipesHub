# Order Management System - Java

This project implements an Order Management System as described in the NCS assignment.

## Features
- Sends Logon/Logout messages at configured trading window.
- Accepts order requests and applies throttling (X orders per second).
- Queues excess orders.
- Supports Modify and Cancel logic in queue.
- Matches responses and logs latency.

## How to Run
1. Compile all Java files in `src/`
2. Run `Main.java`

## Assumptions
- Configured window: 10 AM to 1 PM IST
- Throttle limit: 100 orders/second
- Responses logged to console
