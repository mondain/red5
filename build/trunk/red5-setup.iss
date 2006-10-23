#ifexist "isxdl.iss"
#include "isxdl.iss"
#define DOWNLOAD_SAMPLES
#endif

#ifndef build_dir
#define build_dir "."
#endif
#ifndef root_dir
#define root_dir ".."
#endif
#ifndef version
#define version "test"
#endif
[Setup]
AppName=Red5
AppVerName=Red5 {#version}
AppPublisher=Red5 Project
AppPublisherURL=http://www.osflash.org/red5
DefaultDirName={pf}\Red5
DefaultGroupName=Red5
OutputBaseFilename=setup-red5-{#version}
Compression=lzma
SolidCompression=yes
;Compression=none
WizardSmallImageFile={#build_dir}\images\red5_top.bmp
WizardImageFile={#build_dir}\images\red5_left.bmp
LicenseFile={#root_dir}\license.txt

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[CustomMessages]
RegisterService=&Register as service
StartService=&Start service
JavaSetup=Java Setup
JavaHome=Java Home
JavaHomeDesc=Red5 needs a Java Runtime Environment (JRE) 1.5 to work properly.
JavaHomeInfo=Enter the path to your Java installation.
InvalidJavaHome=The path you selected is invalid. Please make sure a java.exe exists inside the "bin" directory.
MainFiles=Main files
JavaSources=Java source files
FlashSources=Flash sample source files
Red5Services=Network services
Red5ServicesSetup=Network services setup
RedServicesSetupInfo=Select the network services you want to enable in your Red5 installation.
RTMP=RTMP
RTMPT=RTMPT
HTTP=HTTP servlet engine
Debug=Debug proxy
PortWithDefault=port (default %1)
PortWithNumber=port %1
LimitService=You can limit a service to a single IP address by using the format "<ip>:<port>", e.g. "127.0.0.1:1935". If no ip is specified, the service will listen on all available interfaces.
AdminUsernamePassword=To access the administration interface, use "admin" as username and "admin" as password when prompted for login credentials.
#ifdef DOWNLOAD_SAMPLES
DownloadSampleStreams=Download sample streams
#endif

[Components]
Name: "main"; Description: "{cm:MainFiles}"; Types: full compact custom; Flags: fixed
Name: "java_source"; Description: "{cm:JavaSources}"; Types: full
Name: "flash_source"; Description: "{cm:FlashSources}"; Types: full

[Tasks]
Name: "service"; Description: "{cm:RegisterService}"
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; Flags: unchecked
#ifdef DOWNLOAD_SAMPLES
Name: "sample_streams"; Description: "{cm:DownloadSampleStreams}"
#endif

[Files]
; Application files
Source: "{#root_dir}\license.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#root_dir}\red5.jar"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "{#root_dir}\conf\*"; DestDir: "{app}\conf"; Excludes: "red5.properties"; Flags: onlyifdoesntexist recursesubdirs
Source: "{#root_dir}\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs
Source: "{#root_dir}\swf\DEV_Deploy\*"; DestDir: "{app}\swf"; Flags: ignoreversion recursesubdirs
#ifdef DOWNLOAD_SAMPLES
Source: "{#root_dir}\webapps\*"; DestDir: "{app}\webapps"; Excludes: "oflaDemo\streams\*.flv,oflaDemo\streams\*.mp3"; Flags: onlyifdoesntexist recursesubdirs
#else
Source: "{#root_dir}\webapps\*"; DestDir: "{app}\webapps"; Flags: onlyifdoesntexist recursesubdirs
#endif
Source: "{#root_dir}\doc\*"; DestDir: "{app}\doc"; Flags: ignoreversion recursesubdirs

; Files required for windows service / wrapped start
Source: "{#build_dir}\bin\*.bat"; DestDir: "{app}\wrapper"; Flags: ignoreversion
Source: "{#build_dir}\bin\wrapper.exe"; DestDir: "{app}\wrapper"; Flags: ignoreversion
Source: "{#build_dir}\conf\wrapper.conf"; DestDir: "{app}\conf"; Flags: ignoreversion; AfterInstall: UpdateWrapperConf('{app}\conf\wrapper.conf')
Source: "{#build_dir}\lib\wrapper.dll"; DestDir: "{app}\lib"; Flags: ignoreversion
Source: "{#build_dir}\lib\wrapper.jar"; DestDir: "{app}\lib"; Flags: ignoreversion

; Java source code (optional)
Source: "{#root_dir}\.classpath"; DestDir: "{app}"; Flags: ignoreversion; Components: java_source
Source: "{#root_dir}\.project"; DestDir: "{app}"; Flags: ignoreversion; Components: java_source
Source: "{#root_dir}\.springBeans"; DestDir: "{app}"; Flags: ignoreversion; Components: java_source
Source: "{#root_dir}\build.xml"; DestDir: "{app}"; Flags: ignoreversion; Components: java_source
Source: "{#root_dir}\red5.bat"; DestDir: "{app}"; Flags: ignoreversion; Components: java_source
Source: "{#root_dir}\red5.sh"; DestDir: "{app}"; Flags: ignoreversion; Components: java_source
Source: "{#root_dir}\src\*"; DestDir: "{app}\src"; Flags: ignoreversion recursesubdirs; Components: java_source

; Flash sample source code (optional)
Source: "{#root_dir}\swf\DEV_Source\*"; DestDir: "{app}\swf"; Flags: ignoreversion recursesubdirs; Components: flash_source

[Dirs]
Name: "{app}\logs"

[Icons]
Name: "{group}\Red5"; Filename: "{app}\wrapper\Red5.bat"
Name: "{group}\Readme"; Filename: "{app}\doc\readme.html"
Name: "{group}\API Documentation"; Filename: "{app}\doc\api\index.html"
Name: "{group}\Eclipse Setup"; Filename: "{app}\doc\eclipsesetup.html"
Name: "{group}\FAQ (PDF)"; Filename: "{app}\doc\Frequently Asked Questions.pdf"
Name: "{group}\FAQ (Word)"; Filename: "{app}\doc\Frequently Asked Questions.doc"
Name: "{group}\FAQ (Flash)"; Filename: "{app}\doc\Frequently Asked Questions.swf"
Name: "{group}\HOWTO create new applications"; Filename: "{app}\doc\HOWTO-NewApplications.txt"
Name: "{group}\Red5 migration guide"; Filename: "{app}\doc\MigrationGuide.txt"
Name: "{group}\RTMPT Specification"; Filename: "{app}\doc\SPEC-RTMPT.txt"
Name: "{group}\License"; Filename: "{app}\doc\licenseInfo\Red5LicenseInfo.txt"
Name: "{group}\Team"; Filename: "{app}\doc\licenseInfo\team.txt"
Name: "{group}\Mailing list"; Filename: "http://osflash.org/mailman/listinfo/red5_osflash.org"
Name: "{group}\Administration Interface"; Filename: "{code:GetAdminUrl}"
Name: "{group}\Welcome page"; Filename: "{code:GetWelcomeUrl}"

Name: "{group}\{cm:UninstallProgram,Red5}"; Filename: "{uninstallexe}"
Name: "{userdesktop}\Red5"; Filename: "{app}\wrapper\Red5.bat"; Tasks: desktopicon

[Run]
Filename: "{app}\wrapper\InstallRed5-NT.bat"; Tasks: service; Flags: runhidden;
Filename: "{app}\wrapper\StartRed5-NT.bat"; Description: "{cm:StartService}"; Tasks: service; Flags: postinstall runhidden;
Filename: "{app}\wrapper\Red5.bat"; Description: "{cm:LaunchProgram,Red5}"; Tasks: not service; Flags: nowait postinstall skipifsilent

[UninstallRun]
Filename: "{app}\wrapper\StopRed5-NT.bat"; Tasks: service; Flags: runhidden;
Filename: "{app}\wrapper\UninstallRed5-NT.bat"; Tasks: service; Flags: runhidden;

[UninstallDelete]
Type: dirifempty; Name: "{app}\logs"

[Code]
#ifdef DOWNLOAD_SAMPLES
const
  STREAM_DOWNLOAD_URL = 'http://dl.fancycode.com/red5/sample_streams/';
#endif

var
  JavaHome: String;
  JavaHomePage: TInputDirWizardPage;
  ServicesPage: TWizardPage;
  EnableRTMP: TCheckBox;
  PortRTMP: TEdit;
  EnableRTMPT: TCheckBox;
  PortRTMPT: TEdit;
  EnableHTTP: TCheckBox;
  PortHTTP: TEdit;
  EnableDebug: TCheckBox;
  PortDebug: TEdit;

function InitializeSetup(): Boolean;
begin
  Result := False;
  // Check Java 1.5 installation
  if not RegQueryStringValue(HKEY_LOCAL_MACHINE, 'SOFTWARE\JavaSoft\Java Runtime Environment\1.5', 'JavaHome', JavaHome) then
    JavaHome := '';

  Result := True;
end;

procedure URLLabelOnClick(Sender: TObject);
var
  Dummy: Integer;
begin
  ShellExec('open', 'http://osflash.org/red5', '', '', SW_SHOWNORMAL, ewNoWait, Dummy);
end;

Function AddService(Root: TWizardPage; X: Integer; Y: Integer; Name: String; DefaultPort: String; var CB: TCheckBox; var Input: TEdit): Integer;
var
  Static: TNewStaticText;
begin
  CB := TCheckBox.Create(Root);
  {
  CB.Caption := ExpandConstant(Name);
  CB.Top := Y;
  CB.Left := X;
  CB.Parent := Root.Surface;
  CB.Width := Root.SurfaceWidth;
  }
  CB.Checked := True;

  Static := TNewStaticText.Create(Root);
  Static.Caption := ExpandConstant(Name) + ' ' + ExpandConstant('{cm:PortWithDefault,'+DefaultPort+'}');
  Static.Top := Y;
  Static.Left := X;
  Static.Parent := Root.Surface;
  Static.AutoSize := True;

  Input := TEdit.Create(Root);
  Input.Text := DefaultPort;
  Input.Top := Static.Top + Static.Height + 2;
  Input.Left := X;
  Input.Parent := Root.Surface;
  
  Result := Input.Top + Input.Height;
end;

procedure InitializeWizard();
var
  URLLabel: TNewStaticText;
  CancelButton: TButton;
  Pos, X: Integer;
  Static: TNewStaticText;
begin
  // Add link to Red5 homepage on the wizard form
  CancelButton := WizardForm.CancelButton;
  URLLabel := TNewStaticText.Create(WizardForm);
  URLLabel.Left := WizardForm.ClientWidth - CancelButton.Left - CancelButton.Width;
  URLLabel.Top := CancelButton.Top;
  URLLabel.Caption := 'http://osflash.org/red5';
  URLLabel.Font.Style := URLLabel.Font.Style + [fsUnderLine];
  URLLabel.Font.Color := clBlue;
  URLLabel.Cursor := crHand;
  URLLabel.OnClick := @URLLabelOnClick;
  URLLabel.Parent := WizardForm;

  // Custom page to select JAVA_HOME
  JavaHomePage := CreateInputDirPage(wpSelectTasks,
    ExpandConstant('{cm:JavaSetup}'),
    ExpandConstant('{cm:JavaHomeDesc}'),
    ExpandConstant('{cm:JavaHomeInfo}'),
    False,
    '');
  JavaHomePage.Add('');
  JavaHomePage.Values[0] := JavaHome;
  
  // Setup page containing services selection
  ServicesPage := CreateCustomPage(JavaHomePage.ID,
    ExpandConstant('{cm:Red5ServicesSetup}'),
    ExpandConstant('{cm:RedServicesSetupInfo}'));
    
  X := ServicesPage.SurfaceWidth / 2;
  Pos := AddService(ServicesPage, 0, 0, '{cm:RTMP}', '1935', EnableRTMP, PortRTMP);
  AddService(ServicesPage, X, 0, '{cm:RTMPT}', '8088', EnableRTMPT, PortRTMPT);
  
  AddService(ServicesPage, 0, Pos + 16, '{cm:HTTP}', '5080', EnableHTTP, PortHTTP);
  Pos := AddService(ServicesPage, X, Pos + 16, '{cm:Debug}', '1936', EnableDebug, PortDebug);

  Static := TNewStaticText.Create(ServicesPage);
  Static.Parent := ServicesPage.Surface;
  Static.Left := 0;
  Static.Top := Pos + 16;
  Static.Width := ServicesPage.SurfaceWidth;
  Static.Height := Static.Height * 3;
  Static.AutoSize := False;
  Static.WordWrap := True;
  Static.Caption := ExpandConstant('{cm:LimitService}');

  Static := TNewStaticText.Create(ServicesPage);
  Static.Parent := ServicesPage.Surface;
  Static.Left := 0;
  Static.Top := Pos + 64;
  Static.Width := ServicesPage.SurfaceWidth;
  Static.Height := Static.Height * 3;
  Static.AutoSize := False;
  Static.WordWrap := True;
  Static.Caption := ExpandConstant('{cm:AdminUsernamePassword}');
end;

function IsValidJavaHome(Path: String): Boolean;
begin
  Path := AddBackslash(Path);
  Result := FileExists(Path + 'bin\java.exe');
end;

#ifdef DOWNLOAD_SAMPLES
procedure AddDownloadFile(Name: String);
var
  Filename: String;
begin
  FileName := ExpandConstant('{app}\webapps\oflaDemo\streams\' + Name);
  if (FileExists(FileName)) then
    // File already exists
    exit;

  isxdl_AddFile(STREAM_DOWNLOAD_URL + Name, FileName);
end;

function DownloadFiles(): Boolean;
var
  Wnd: Integer;
  Tasks: String;
begin
  Wnd := StrToInt(ExpandConstant('{wizardhwnd}'));
  Tasks := WizardSelectedTasks(False);
  isxdl_ClearFiles();

  if Pos('sample_streams', Tasks) > 0 then begin
    if not FileExists(ExpandConstant('{app}\webapps\oflaDemo\streams')) then
      ForceDirectories(ExpandConstant('{app}\webapps\oflaDemo\streams'));

    AddDownloadFile('on2_flash8_w_audio.flv');
    AddDownloadFile('Spiderman3_trailer_300.flv');
  end;

  Result := (isxdl_DownloadFiles(Wnd) <> 0);
end;
#endif

function NextButtonClick(CurPage: Integer): Boolean;
begin
  Result := True;
  if (CurPage = JavaHomePage.ID) then begin
    if not IsValidJavaHome(JavaHomePage.Values[0]) then begin
      MsgBox(ExpandConstant('{cm:InvalidJavaHome}'), mbError, MB_OK);
      Result := False;
    end;
  end;
end;

function UpdateReadyMemo(Space, NewLine, MemoUserInfoInfo, MemoDirInfo, MemoTypeInfo, MemoComponentsInfo, MemoGroupInfo, MemoTasksInfo: String): String;
begin
  Result := MemoDirInfo + NewLine + NewLine +
            MemoGroupInfo + NewLine + NewLine;

  if (MemoComponentsInfo <> '') then
    Result := Result + MemoComponentsInfo + NewLine + NewLine;

  if (MemoTasksInfo <> '') then
    Result := Result + MemoTasksInfo + NewLine + NewLine;

  Result := Result +
    ExpandConstant('{cm:JavaHome}') + ':' + NewLine +
    Space + AddBackslash(JavaHomePage.Values[0]) + NewLine + NewLine;
    
  Result := Result +
    ExpandConstant('{cm:Red5Services}') + ':' + NewLine;
  if (EnableRTMP.Checked) then
    Result := Result + Space + ExpandConstant('{cm:RTMP}') + ' ' + ExpandConstant('{cm:PortWithNumber,'+PortRTMP.Text+'}') + NewLine;
  if (EnableRTMPT.Checked) then
    Result := Result + Space + ExpandConstant('{cm:RTMPT}') + ' ' + ExpandConstant('{cm:PortWithNumber,'+PortRTMPT.Text+'}') + NewLine;
  if (EnableHTTP.Checked) then
    Result := Result + Space + ExpandConstant('{cm:HTTP}') + ' ' + ExpandConstant('{cm:PortWithNumber,'+PortHTTP.Text+'}') + NewLine;
  if (EnableDebug.Checked) then
    Result := Result + Space + ExpandConstant('{cm:Debug}') + ' ' + ExpandConstant('{cm:PortWithNumber,'+PortDebug.Text+'}') + NewLine;
end;

procedure UpdateWrapperConf(Filename: String);
var
  Lines: TArrayOfString;
  i: Integer;
  Path: String;
begin
  Filename := ExpandConstant(Filename);
  Path := AddBackslash(JavaHomePage.Values[0]);
  if LoadStringsFromFile(Filename, Lines) then begin
    for i := 0 to GetArrayLength(Lines)-1 do begin
      if Pos(Path, Lines[i]) > 0 then
        // Already changed this line...
        continue;

      if Pos('wrapper.java.command=', Lines[i]) > 0 then begin
        Lines[i] := Format('wrapper.java.command=%sbin\java.exe', [Path]);
      end;
    end;
    SaveStringsToFile(Filename, Lines, False);
  end;
end;

procedure AdjustHostPort(Input: TEdit);
begin
  if (Pos(':', Input.Text) = 0) then
    Input.Text := '0.0.0.0:' + Input.Text;
end;

procedure SplitHostPort(Input: TEdit; var Host: String; var Port: String);
var
  Idx: Integer;
begin
  AdjustHostPort(Input);
  Idx := Pos(':', Input.Text);
  Host := Copy(Input.Text, 1, Idx-1);
  Port := Copy(Input.Text, Idx+1, Length(Input.Text)-Idx);
end;

procedure CurStepChanged(CurStep: TSetupStep);
var
  Filename: String;
  Host, Port: String;
begin
  if (CurStep = ssPostInstall) then begin
#ifdef DOWNLOAD_SAMPLES
    DownloadFiles();
#endif
    Filename := ExpandConstant('{app}\conf\red5.properties');
    if FileExists(Filename) then
      exit;
      
    AdjustHostPort(PortRTMP);
    SaveStringToFile(Filename, Format('rtmp.host_port = %s', [PortRTMP.Text]) + #13#10, False);
    
    SplitHostPort(PortHTTP, Host, Port);
    SaveStringToFile(Filename, Format('http.host = %s', [Host]) + #13#10, True);
    SaveStringToFile(Filename, Format('http.port = %s', [Port]) + #13#10, True);
    
    SplitHostPort(PortRTMPT, Host, Port);
    SaveStringToFile(Filename, Format('rtmpt.host = %s', [Host]) + #13#10, True);
    SaveStringToFile(Filename, Format('rtmpt.port = %s', [Port]) + #13#10, True);
    
    SaveStringToFile(Filename, Format('debug_proxy.host_port = %s', [PortDebug.Text]) + #13#10, True);
    SplitHostPort(PortHTTP, Host, Port);
    if (Host = '') or (Host = '0.0.0.0') then
      Host := '127.0.0.1';
    SaveStringToFile(Filename, Format('proxy_forward.host_port = %s:%s', [Host, Port]) + #13#10, True);
  end;
end;

function GetAdminUrl(Default: String): String;
var
  Host, Port: String;
begin
  SplitHostPort(PortHTTP, Host, Port);
  if (Host = '') or (Host = '0.0.0.0') then
    Host := '127.0.0.1';
  Result := Format('http://%s:%s/admin', [Host, Port]);
end;

function GetWelcomeUrl(Default: String): String;
var
  Host, Port: String;
begin
  SplitHostPort(PortHTTP, Host, Port);
  if (Host = '') or (Host = '0.0.0.0') then
    Host := '127.0.0.1';
  Result := Format('http://%s:%s/', [Host, Port]);
end;

