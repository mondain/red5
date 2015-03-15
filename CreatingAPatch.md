# Creating a patch from SVN #

For information about how to run from within Eclipse of subversion, work is still needed. Create a patch of change file by running the following command from the top of the Red5 directory in the terminal. Submit your patch via the mailing list or by creating an issue.

```
svn diff src/org/red5/server/util/FileUtil.java> FileUtil.patch
```

The patch file that you created in this step, you can submit to the ticket as a file upload.