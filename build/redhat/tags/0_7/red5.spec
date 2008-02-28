Summary: Red5 Server
Name: red5
Version: 0.7.0
Release: 1
Source0: %{name}-%{version}.tar.gz
License: LGPL
Group: Applications/Networking
BuildRoot: %{_builddir}/%{name}-root
%description
The Red5 open source Flash server allows you to record and stream video to the Flash Player.
%prep
%setup -q
%build
ant dist-installer
%install
cp dist $RPM_BUILD_ROOT
%clean
rm -rf $RPM_BUILD_ROOT
%files
%defattr(-,root,root)
/usr/local/bin/red5.init
%doc doc/changelog.txt
