Ledger Wallet Java Card Eligibility applet
==========================================

This applet let you test the performance of your Java Card platform to run a Hierarchical Deterministic Bitcoin Hardware Wallet implementation. 

It tries to find out all available algorithms, provide a pure Java implementation for the missing ones and gives a detailed report on support and performance.

If you have access to proprietary APIs, you can easily enable additional tests by providing an implementation of the ProprietaryAPI class 

For any question or commercial licensing, reach us at hello@ledger.fr
For technical question, open an issue on the repo.

Sample report on a Yubikey Neo 
-------------------------------

```
Test Elliptic Curves ... : OK (454 ms)
        Key generation supported
        Secure random supported
Test Public Key recovery ... : OK (115 ms)
        Partial key recovery supported
Test RIPEMD160 ... : OK (284 ms)
Test SHA512 ... : OK (934 ms)
Test HD Seed Generation ... : OK (3683 ms)
Test Hardened derivation ... : OK (3693 ms)
```

