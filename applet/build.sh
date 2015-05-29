# Replace with the path to your Java Card Development Kit
JCENV=/media/truecrypt1/standards/JC/JavaCardKit-3_0_2/
mkdir -p build/classes build/applet build/src/com/ledger/eligibility 2>/dev/null
cp src/com/ledger/eligibility/*.java build/src/com/ledger/eligibility
cpp -P src/com/ledger/eligibility/Ripemd160.javap > build/src/com/ledger/eligibility/Ripemd160.java
cpp -P src/com/ledger/eligibility/SHA512.javap > build/src/com/ledger/eligibility/SHA512.java
cpp -P src/com/ledger/eligibility/LedgerEligibility.javap > build/src/com/ledger/eligibility/LedgerEligibility.java
javac -g -classpath $JCENV/lib/api_classic.jar -sourcepath build/src -d build/classes build/src/com/ledger/eligibility/*.java 
java -classpath "$JCENV/lib/*" com.sun.javacard.converter.Main -exportpath "$JCENV/api_export_files" -useproxyclass -out CAP -classdir build/classes -d build/applet -applet 0xFF:0x4C:0x45:0x47:0x52:0x2E:0x45:0x4C:0x49:0x47:0x30:0x31:0x2E:0x49:0x30:0x31 com.ledger.eligibility.LedgerEligibility com.ledger.eligibility 0xFF:0x4C:0x45:0x47:0x52:0x2E:0x45:0x4C:0x49:0x47:0x30:0x31 1.0
cp build/applet/com/ledger/eligibility/javacard/eligibility.cap build/Ledger-eligibility.cap 2>/dev/null
rm -rf build/applet
rm -rf build/classes
rm -rf build/src

