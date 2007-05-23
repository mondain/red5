#
# How to release:
# 1. Get the latest tarball or tag from svn of the Red5 server and unpack it. 
# 2. Change the SRC_DIR below and point it to the root of the unpacked Red5 installation directory.
# 3. Updating the version number isn't automated and optimized yet and now has to be done manually.
#    So before building, first:
#    - open the file 'packages/docs/Info.plist', change the version numbers and save the file.
#    - open the file 'packages/flash/Info.plist', change the version numbers and save the file.
#    - open the file 'packages/main/Info.plist', change the version numbers and save the file.
#    - open the file 'packages/source/Info.plist', change the version numbers and save the file.
#    - open the file 'bin/Red5.app/Contents/Info.plist', change the version numbers and save the file.
#    - open the file 'bin/Red5.app/Contents/Resources/en.lproj/InfoPlist.strings', change the version numbers and save the file.
#    - open the file 'bin/Welcome.txt', change the version number and save the file.
#    - change the VERSION below (don't use any spaces)
# 4. Run this file with 'make'.
# 5. The output is the setup-red5xx.dmg file and a folder with a copy of the package's content.
#
# Thijs Triemstra, 05/2007
# Original script from http://brok3n.org/archivesextreme/2006/09/dmg_creation_ti.html

VERSION=0.6.1
RELEASE_NAME=red5-$(VERSION)
FANCY_NAME=Red5 v$(VERSION)
DMG_NAME=setup-$(RELEASE_NAME).dmg
MPKG_NAME=Install-$(RELEASE_NAME).mpkg
SRC_DIR=../stable/$(RELEASE_NAME)
BUILD_DIR=build
BIN_DIR=bin
PACKAGES_DIR=packages
RED5_DOCS_PACKAGE_NAME=docs
RED5_DOCS_PACKAGE=$(PACKAGES_DIR)/$(RED5_DOCS_PACKAGE_NAME)
RED5_FLASH_PACKAGE_NAME=flash
RED5_FLASH_PACKAGE=$(PACKAGES_DIR)/$(RED5_FLASH_PACKAGE_NAME)
RED5_MAIN_PACKAGE_NAME=main
RED5_MAIN_PACKAGE=$(PACKAGES_DIR)/$(RED5_MAIN_PACKAGE_NAME)
RED5_SOURCE_PACKAGE_NAME=source
RED5_SOURCE_PACKAGE=$(PACKAGES_DIR)/$(RED5_SOURCE_PACKAGE_NAME)
RED5_META_PACKAGE_NAME=release
RED5_META_PACKAGE=$(PACKAGES_DIR)/$(RED5_META_PACKAGE_NAME)
RED5_DIR=$(BUILD_DIR)/Red5

.PHONY: all info compile clean release diskimage

all: info clean compile release diskimage

info: 
	@echo == Starting Mac OSX packager for Red5 ==

compile:
	@echo == Compiling $(FANCY_NAME) with Ant...
	$(MAKE) -C $(SRC_DIR)
	@echo == Build $(FANCY_NAME) successful.

clean:
	@echo == Removing old build directory...
	rm -rf $(BUILD_DIR)

release:
	@echo == Removing old build directory...
	@# Clean build directory
	rm -rf $(BUILD_DIR)
	
	@echo == Creating new build directory...	
	mkdir $(BUILD_DIR)

	@# This directory mirrors the contents of the package
	mkdir $(RED5_DIR)

	@echo == Creating directories for packages...
	@# create temporary packages directory
	mkdir $(BUILD_DIR)/$(PACKAGES_DIR)

	mkdir $(BUILD_DIR)/$(RED5_DOCS_PACKAGE_NAME)
	mkdir $(BUILD_DIR)/$(RED5_FLASH_PACKAGE_NAME)
	mkdir $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	mkdir $(BUILD_DIR)/$(RED5_SOURCE_PACKAGE_NAME)
	mkdir $(BUILD_DIR)/$(RED5_META_PACKAGE_NAME)

	@echo == Copying files for '$(RED5_DOCS_PACKAGE_NAME)' package...
	cp -r $(SRC_DIR)/doc $(BUILD_DIR)/$(RED5_DOCS_PACKAGE_NAME)
	
	@echo == Copying files for '$(RED5_FLASH_PACKAGE_NAME)' package...
	mkdir $(BUILD_DIR)/$(RED5_FLASH_PACKAGE_NAME)/swf
	cp -r $(SRC_DIR)/swf $(BUILD_DIR)/$(RED5_FLASH_PACKAGE_NAME)

	@echo == Copying files for '$(RED5_MAIN_PACKAGE_NAME)' package...
	cp -r $(SRC_DIR)/conf $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	cp -r $(SRC_DIR)/lib $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	cp $(SRC_DIR)/license.txt $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	cp $(SRC_DIR)/red5.jar $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	cp $(SRC_DIR)/red5.sh $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	cp $(SRC_DIR)/red5-shutdown.sh $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	cp -r $(SRC_DIR)/webapps $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)

	@# Copy Red5 launcher
	cp -R $(BIN_DIR)/Red5.app $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	/Developer/Tools/SetFile -a E $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)/Red5.app

	@echo == Copying files for '$(RED5_SOURCE_PACKAGE_NAME)' package...
	cp $(SRC_DIR)/build.properties $(BUILD_DIR)/$(RED5_SOURCE_PACKAGE_NAME)
	cp $(SRC_DIR)/build.xml $(BUILD_DIR)/$(RED5_SOURCE_PACKAGE_NAME)
	cp -r $(SRC_DIR)/src $(BUILD_DIR)/$(RED5_SOURCE_PACKAGE_NAME)
	cp -r $(SRC_DIR)/test $(BUILD_DIR)/$(RED5_SOURCE_PACKAGE_NAME)

	echo == Copying files for $(RED5_META_PACKAGE_NAME) package...
	@# Copy installer files (english version)
	mkdir $(BUILD_DIR)/$(RED5_META_PACKAGE_NAME)/English.lproj
	cp -R $(BIN_DIR)/background.gif $(BUILD_DIR)/$(RED5_META_PACKAGE_NAME)/English.lproj
	cp -R $(BIN_DIR)/Welcome.txt $(BUILD_DIR)/$(RED5_META_PACKAGE_NAME)/English.lproj

	@# Add fresh copy of license
	cp $(SRC_DIR)/license.txt $(BUILD_DIR)/$(RED5_META_PACKAGE_NAME)/English.lproj/License.txt

	@# Add fresh copy of change log
	cp $(SRC_DIR)/doc/changelog.txt $(BUILD_DIR)/$(RED5_META_PACKAGE_NAME)/English.lproj/ReadMe.txt

	@#                         PackageMaker Options
	@#
	@# .pkg options
	@# -p destination-path     package product (REQUIRED)
	@# -f root-path            package contents directory
	@# -b build-path           temporary build directory (if splitting forks)
	@# -s                      split resource forks (reassembled by Installer)
	@# -ds                     filter out .DS_Store files
	@# -v                      verbose output
	@# -r resources-path       resources directory
	@# -i info-path            premade Info.plist file
	@# -d desc-path            premade Description.plist file
	@# -u                      Do not compress archive
	@#	
	@# meta-package-options:
	@#
	@# -mi package-directory   Directory of packages to use to create a
	@#                         metapackage with internal packages
	@# -ms package-directory   Directory of packages to use to create a
	@#                         metapackage with same-level packages
	@# -mc package-directory   Directory of packages to use to create a
	@#                         metapackage with custom location packages
	@# -proj:
	@# -proj project-path      .pmproj file created by PackageMaker. Overrides
	@#                         all other options except -p and -v.
	@# -snapshot:
	@# -snapshot snapshot-path .snapshot directory created by PackageMaker.
	@#                         Overrides all other options except -p, -i, and -v.
		
	@echo == Creating $(RED5_DOCS_PACKAGE_NAME) package...
	@# Create docs package file
	/Developer/Tools/packagemaker -build -p $(BUILD_DIR)/$(PACKAGES_DIR)/$(RED5_DOCS_PACKAGE_NAME).pkg -f $(BUILD_DIR)/$(RED5_DOCS_PACKAGE_NAME) -ds -v -i $(RED5_DOCS_PACKAGE)/Info.plist -d $(RED5_DOCS_PACKAGE)/Description.plist
	
	@echo == Creating $(RED5_FLASH_PACKAGE_NAME) package...
	@# Create flash package file
	/Developer/Tools/packagemaker -build -p $(BUILD_DIR)/$(PACKAGES_DIR)/$(RED5_FLASH_PACKAGE_NAME).pkg -f $(BUILD_DIR)/$(RED5_FLASH_PACKAGE_NAME) -ds -v -i $(RED5_FLASH_PACKAGE)/Info.plist -d $(RED5_FLASH_PACKAGE)/Description.plist 

	@echo == Creating $(RED5_MAIN_PACKAGE_NAME) package...
	@# Create main package file
	/Developer/Tools/packagemaker -build -p $(BUILD_DIR)/$(PACKAGES_DIR)/$(RED5_MAIN_PACKAGE_NAME).pkg -f $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME) -ds -v -i $(RED5_MAIN_PACKAGE)/Info.plist -d $(RED5_MAIN_PACKAGE)/Description.plist 

	@echo == Creating $(RED5_SOURCE_PACKAGE_NAME) package...
	@# Create source package file
	/Developer/Tools/packagemaker -build -p $(BUILD_DIR)/$(PACKAGES_DIR)/$(RED5_SOURCE_PACKAGE_NAME).pkg -f $(BUILD_DIR)/$(RED5_SOURCE_PACKAGE_NAME) -ds -v -i $(RED5_SOURCE_PACKAGE)/Info.plist -d $(RED5_SOURCE_PACKAGE)/Description.plist 

	@echo == Creating $(RELEASE_NAME) metapackage...
	@# Create metapackage file
	/Developer/Tools/packagemaker -build -p $(RED5_DIR)/$(MPKG_NAME) -f $(RED5_DIR) -mi $(BUILD_DIR)/$(PACKAGES_DIR) -ds -v -i $(RED5_META_PACKAGE)/Info.plist -d $(RED5_META_PACKAGE)/Description.plist -r $(BUILD_DIR)/$(RED5_META_PACKAGE_NAME)

	@echo == Removing old build files...
	@# cleanup
	rm -rf $(BUILD_DIR)/$(RED5_DOCS_PACKAGE_NAME)
	rm -rf $(BUILD_DIR)/$(RED5_FLASH_PACKAGE_NAME)
	rm -rf $(BUILD_DIR)/$(RED5_MAIN_PACKAGE_NAME)
	rm -rf $(BUILD_DIR)/$(RED5_SOURCE_PACKAGE_NAME)
	rm -rf $(BUILD_DIR)/$(RED5_META_PACKAGE_NAME)
	rm -rf $(BUILD_DIR)/$(PACKAGES_DIR)

	@echo == Deleting SVN and backup files...
	@# delete svn and backup files
	find $(BUILD_DIR) -name ".svn" -type d -exec rm -rf {} \; -prune
	find $(BUILD_DIR) \( -name "*~" -or -name .DS_Store -or -name classes.nib -or -name info.nib \) -type f -delete

diskimage:
	@echo == Creating disk image...
	@# make Red5 disk image
	./make-diskimage.sh "$(BUILD_DIR)/$(DMG_NAME)" "$(RED5_DIR)" "Red5" dmg_red5.scpt
	
	@# Dump md5 checksum for dmg file
	@echo `md5 $(BUILD_DIR)/$(DMG_NAME)`

	@echo == Finished building $(FANCY_NAME)
