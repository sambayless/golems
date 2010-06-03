# Auto-generated by EclipseNSIS Script Wizard
# Nov 21, 2008 1:13:05 PM

Name Golems

RequestExecutionLevel admin

# General Symbol Definitions
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 0.56.0
!define COMPANY "Sam Bayless"
!define URL www.golemgame.com

!define JRE_VERSION "1.5"
#!define JRE_URL # "http://javadl.sun.com/webapps/download/AutoDL?BundleId=18714&/jre-6u5-windows-i586-p.exe"

# MUI Symbol Definitions
!define MUI_ICON golems.ico
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${REGKEY}
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULTFOLDER Golems
!define MUI_UNICON modern-uninstall-blue-full.ico
!define MUI_UNFINISHPAGE_NOAUTOCLOSE

# Included files
!include Sections.nsh
!include MUI2.nsh

# Variables
Var StartMenuGroup


# Installer pages
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Installer languages
!insertmacro MUI_LANGUAGE English





!define registerExtension "!insertmacro registerExtension"
!define unregisterExtension "!insertmacro unregisterExtension"
 
!macro registerExtension executable extension description
       Push "${executable}"  ; "full path to my.exe"
       Push "${extension}"   ;  ".mkv"
       Push "${description}" ;  "MKV File"
       Call registerExtension
!macroend
 
; back up old value of .opt
Function registerExtension
!define Index "Line${__LINE__}"
  pop $R0 ; ext name
  pop $R1
  pop $R2
  push $1
  push $0
  ReadRegStr $1 HKCR $R1 ""
  StrCmp $1 "" "${Index}-NoBackup"
    StrCmp $1 "OptionsFile" "${Index}-NoBackup"
    WriteRegStr HKCR $R1 "backup_val" $1
"${Index}-NoBackup:"
  WriteRegStr HKCR $R1 "" $R0
  ReadRegStr $0 HKCR $R0 ""
  StrCmp $0 "" 0 "${Index}-Skip"
    WriteRegStr HKCR $R0 "" $R0
    WriteRegStr HKCR "$R0\shell" "" "open"
    WriteRegStr HKCR "$R0\DefaultIcon" "" "$R2,0"
"${Index}-Skip:"
  WriteRegStr HKCR "$R0\shell\open\command" "" '$R2 "%1"'
  WriteRegStr HKCR "$R0\shell\edit" "" "Edit $R0"
  WriteRegStr HKCR "$R0\shell\edit\command" "" '$R2 "%1"'
  pop $0
  pop $1
!undef Index
FunctionEnd
 
!macro unregisterExtension extension description
       Push "${extension}"   ;  ".mkv"
       Push "${description}"   ;  "MKV File"
       Call un.unregisterExtension
!macroend
 
Function un.unregisterExtension
  pop $R1 ; description
  pop $R0 ; extension
!define Index "Line${__LINE__}"
  ReadRegStr $1 HKCR $R0 ""
  StrCmp $1 $R1 0 "${Index}-NoOwn" ; only do this if we own it
  ReadRegStr $1 HKCR $R0 "backup_val"
  StrCmp $1 "" 0 "${Index}-Restore" ; if backup="" then delete the whole key
  DeleteRegKey HKCR $R0
  Goto "${Index}-NoOwn"
"${Index}-Restore:"
  WriteRegStr HKCR $R0 "" $1
  DeleteRegValue HKCR $R0 "backup_val"
  DeleteRegKey HKCR $R1 ;Delete key with association name settings
"${Index}-NoOwn:"
!undef Index
FunctionEnd

; StrContains
; This function does a case sensitive searches for an occurrence of a substring in a string. 
; It returns the substring if it is found. 
; Otherwise it returns null(""). 
; Written by kenglish_hi
; Adapted from StrReplace written by dandaman32
 
 
Var STR_HAYSTACK
Var STR_NEEDLE
Var STR_CONTAINS_VAR_1
Var STR_CONTAINS_VAR_2
Var STR_CONTAINS_VAR_3
Var STR_CONTAINS_VAR_4
Var STR_RETURN_VAR
 
Function StrContains
  Exch $STR_NEEDLE
  Exch 1
  Exch $STR_HAYSTACK
  ; Uncomment to debug
  ;MessageBox MB_OK 'STR_NEEDLE = $STR_NEEDLE STR_HAYSTACK = $STR_HAYSTACK '
    StrCpy $STR_RETURN_VAR ""
    StrCpy $STR_CONTAINS_VAR_1 -1
    StrLen $STR_CONTAINS_VAR_2 $STR_NEEDLE
    StrLen $STR_CONTAINS_VAR_4 $STR_HAYSTACK
    loop:
      IntOp $STR_CONTAINS_VAR_1 $STR_CONTAINS_VAR_1 + 1
      StrCpy $STR_CONTAINS_VAR_3 $STR_HAYSTACK $STR_CONTAINS_VAR_2 $STR_CONTAINS_VAR_1
      StrCmp $STR_CONTAINS_VAR_3 $STR_NEEDLE found
      StrCmp $STR_CONTAINS_VAR_1 $STR_CONTAINS_VAR_4 done
      Goto loop
    found:
      StrCpy $STR_RETURN_VAR $STR_NEEDLE
      Goto done
    done:
   Pop $STR_NEEDLE ;Prevent "invalid opcode" errors and keep the
   Exch $STR_RETURN_VAR  
FunctionEnd
 
!macro _StrContainsConstructor OUT NEEDLE HAYSTACK
  Push "${HAYSTACK}"
  Push "${NEEDLE}"
  Call StrContains
  Pop "${OUT}"
!macroend
 
!define StrContains '!insertmacro "_StrContainsConstructor"'

# Installer attributes
OutFile Golems-setup.exe
InstallDir $PROGRAMFILES\Golems
CRCCheck on
XPStyle on
ShowInstDetails hide
VIProductVersion "${VERSION}.0"
VIAddVersionKey ProductName Golems
VIAddVersionKey ProductVersion "${VERSION}"
VIAddVersionKey CompanyName "${COMPANY}"
VIAddVersionKey CompanyWebsite "${URL}"
VIAddVersionKey FileVersion "${VERSION}"
VIAddVersionKey FileDescription ""
VIAddVersionKey LegalCopyright ""
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails hide

# Installer sections
Section -Main SEC0000
    SetOutPath $INSTDIR\dist
    SetOverwrite on
    File /r ant\dist\windows\*
    SetOutPath $INSTDIR
    File ant\windows-launcher\Golems.exe
    File ant\windows-launcher\Golems.ini

    
    WriteRegStr HKLM "${REGKEY}\Components" Main 1
    
    
     Call DetectJRE
SectionEnd

Section -post SEC0001
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    SetOutPath $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    
    CreateShortcut "$DESKTOP\$(^Name).lnk" $INSTDIR\Golems.exe
    
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk" $INSTDIR\Golems.exe
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk" $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_END
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1

    ${registerExtension} $INSTDIR\Golems.exe ".mchn" "Golems Machine File"
SectionEnd

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

# Uninstaller sections
Section /o -un.Main UNSEC0000
    Delete /REBOOTOK $INSTDIR\Golems.ini
    Delete /REBOOTOK $INSTDIR\Golems.exe
    RmDir /r /REBOOTOK $INSTDIR\dist
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section -un.post UNSEC0001
    Delete /REBOOTOK  "$DESKTOP\$(^Name).lnk"

    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^Name).lnk"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /REBOOTOK $SMPROGRAMS\$StartMenuGroup
    RmDir /REBOOTOK $INSTDIR
    Push $R0
    StrCpy $R0 $StartMenuGroup 1
    StrCmp $R0 ">" no_smgroup
    
     ${unregisterExtension} ".mchn" "Golems Machine File"
no_smgroup:
    Pop $R0
SectionEnd

# Installer functions
Function .onInit
    InitPluginsDir
FunctionEnd

# Uninstaller functions
Function un.onInit
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuGroup
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
FunctionEnd


Function GetJRE
       
         
    SetOutPath $TEMP
    SetOverwrite on
    File jre-kernel.exe

   ExecWait "$TEMP\jre-kernel.exe"
      Delete "$TEMP\jre-kernel.exe"
       #  StrCpy $2 "$TEMP\Java Runtime Environment.exe"
       # nsisdl::download /TIMEOUT=30000 ${JRE_URL} $2
       # Pop $R0 ;Get the return value
        #        StrCmp $R0 "success" +3
        #        MessageBox MB_OK "Download failed: $R0"
        #        Quit
        #ExecWait $2
        #Delete $2
        
        
        
FunctionEnd
 
 
Function DetectJRE


  
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
            
  
 # ${If} $2 >= ${JRE_VERSION} 
  #greater/equal must be integer comparison only
  #  Goto done
  #${EndIf}
  
 ${StrContains} $4  "1.0" $2 
   ${StrContains} $5  "1.1"  $2
  ${StrContains} $6  "1.2" $2 
   ${StrContains} $7 "1.3"  $2 
    ${StrContains} $8 "1.4"  $2 
 
StrCmp $4 "1.0" badJRE
StrCmp $5 "1.1" badJRE
StrCmp $6 "1.2" badJRE
StrCmp $7 "1.3" badJRE
StrCmp $8 "1.4" badJRE

 Goto done
 
 badJRE:
  MessageBox MB_YESNO "This program requires Java 5 or higher. Would you like to install it now?" IDNO done
    
  Call GetJRE
 
  done:
FunctionEnd


