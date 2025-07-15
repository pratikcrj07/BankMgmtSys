#  Bank Management System & ATM Simulator

A **Java-based Bank Management System** combined with an **ATM Simulator**, designed to replicate core banking operations with a user-friendly desktop interface and modern biometric login using face recognition.

---

##  Project Overview

This project is developed as part of my BCA curriculum to demonstrate practical skills in:

- Java Swing & AWT GUI design
- Database integration using MySQL
- Biometric authentication using OpenCV (planned)
- Secure banking operations simulation

The system operates similar to a real-world ATM, supporting multiple transactions, account management, and user authentication with a clean blue & white themed interface.

---

##  Key Features

- **Account Opening** — Register new users with secure data validation.
- **Face Registration** — Capture and store user face data for biometric login.
- **Face Login** — Log in securely using facial recognition.
- **Deposit & Withdrawal** — Perform core ATM transactions.
- **Mini Statement** — Generate & view transaction history.
- **PIN Change** — Securely change ATM PIN.
- **Modern UI** — Simple, classy design using Swing and AWT.

---

## ⚙Tech Stack

| Layer            | Technology            |
|------------------|-----------------------|
| **Frontend**     | Java Swing, AWT       |
| **Backend**      | Java                  |
| **Database**     | MySQL Workbench       |
| **Biometrics**   | OpenCV (JavaCV)       |
| **Version Control** | Git & GitHub      |

---

##  Project Structure

Bank-ATM-Simulator-Java/
│
├── src/ # Java source code
│ ├── bank/management/ # All banking operations
│ ├── atm/simulator/ # ATM Simulator modules
│ ├── face/recognition/ # Face capture & login
│ └── utils/ # Helper classes, DB connection
│
├── assets/ # Images, icons, face datasets
├── sql/ # Database schema and setup scripts
├── README.md # Project documentation
└── LICENSE # License (MIT recommended)

yaml
Copy
Edit

---

##  Getting Started

###  Prerequisites

- Java JDK 8+  
- MySQL Server + Workbench  
- OpenCV library (Java bindings)  
- IDE (IntelliJ IDEA, Eclipse, or NetBeans)  
- Git

### ⚙ Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/Bank-ATM-Simulator-Java.git
   cd Bank-ATM-Simulator-Java
Configure Database

Create a database in MySQL.

Run the scripts in /sql/ to create required tables.

Update DB credentials in your DBConnection class.

Run the application

Compile & run from your IDE.

Register a user and capture face data.

Login using face recognition or PIN.

Perform transactions.



 Contribution
I welcome contributors to help expand and polish this system. Possible improvements:

Better face recognition pipeline.

Encrypt PINs & sensitive data.

Integrate voice prompts.

Add more banking modules.

Pull requests are welcome.
If you spot a bug or want to suggest a feature, please open an issue.

License
Distributed under the MIT License. See LICENSE for details.
 Contact
For questions
@Pratik_humagain
pratikhumagaincr7@gmail.com

 If you like this project, please give it a star!
