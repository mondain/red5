See also [howto.txt](https://github.com/Red5/installer/blob/master/redhat/howto.txt) for detail.

For Red5-1.0.2 or above:

```
$ sudo yum -y install git rpm-build
$ git clone https://github.com/Red5/installer/ ./red5-installer
$ cd red5-installer/redhat/
$ ls
Makefile  howto.txt  make_rpmPackage.sh  old  red5.init  red5.spec
$ ./make_rpmPackage.sh 1.0.3  # e.g) create red5-1.0.3-1.dist.arch.rpm
```

For Red5-1.0.0, 1.0.1:

  * [RPM\_HowTo\_Old](https://code.google.com/p/red5/wiki/RPM_HowTo_Old)