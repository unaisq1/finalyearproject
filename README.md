# finalyearproject

BEng Software Engineering Final Year Project by Unais Qureshi

NOTE: All current issues on Sprint Kanban Boards were created from 16/03/2025 based on previously made issues that were deleted from a prior repository (therefore sprint tracking did not start on 16/03/2025 but weeks before)

Refer to "Important File Paths.txt" to locate the key files in which code and data are stored.

**HOW TO RUN ARTEFACT:**

1. Ensure all proper files from repository are cloned locally with no issues
2. Open "zaproxy" directory in Terminal
3. Run command "./gradlew run"
4. ZAP Dev Build will open

**PROCESS TO COPY FILE CHANGES TO ARTEFACT:**

1. Save changes made to files in Visual Studio Code
2. Open "zap-extensions" directory in Terminal
3. Run command "./gradlew addOns:tipsandadvice:copyZapAddOn"
4. Changes made will be copied to "zaproxy" directory (ZAP Dev Build)
