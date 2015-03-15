# Flash Video (FLV) #

Flash Video is the name of a file format used to deliver video over the Internet using Adobe Flash Player version 6 or newer. Flash Video content may also be embedded within SWF files. Until version 9 update 3 of the Flash Player, Flash Video referred to a proprietary file format, having the extension .FLV but Adobe introduced [new file extensions and MIME types](http://www.kaourantin.net/2007/10/new-file-extensions-and-mime-types.html) and suggests to use those instead of the old FLV:

| File Extension  | [FTYP](http://ftyps.com/)     | [MIME Type](http://www.adobe.com/go/tn_19439)           | Description                                               |
|:----------------|:------------------------------|:--------------------------------------------------------|:----------------------------------------------------------|
| .f4v         | 'F4V '	       | video/mp4                             | Video for Adobe Flash Player                                             |
| .f4p           | 'F4P '	         | video/mp4                        | Protected Media for Adobe Flash Player                           |
| .f4a             | 'F4A '	 | video/mp4          | Audio for Adobe Flash Player                         |
| .f4b            | 'F4B '	     | video/mp4            | Audio Book for Adobe Flash Player       |
| .flv         |	       | video/x-flv                             | [Flash Video](http://en.wikipedia.org/wiki/Flash_Video)                                           |

It is possible to place H.264 and AAC streams into the traditional FLV file, but Adobe strongly encourages everyone to embrace the new standard file format. There are functional limits with the FLV structure when streaming H.264 which couldn't be overcome without a redesign of the file format. This is one of the reasons Adobe is moving away from the traditional FLV file structure. Specifically dealing with sequence headers and enders is tricky with FLV streams. Adobe is still working out if it's possible to place On2 VP6 streams into the new file format.

## Overview ##
  * File format parser implementing parts of [ISO 14496-12](http://www.iso.org/iso/en/CombinedQueryResult.CombinedQueryResult?queryString=14496-12) (very limited sub set of [MPEG-4](http://en.wikipedia.org/wiki/MPEG-4), 3GP and QuickTime movie support).
  * Support for the 3GPP timed text specification [3GPP TS 26.245](http://www.3gpp.org/ftp/Specs/html-info/26245.htm). Essentially this is a standardized subtitle format within [3GP](http://en.wikipedia.org/wiki/3GP) files. Any number of text tracks are supported and all the information, including esoteric stuff like karaoke meta data is dumped in 'onMetaData' and a new 'onTextData' NetStream callback. Language information in the individual tracks is also reported. That means you can have sub titles in several languages. Check the [3GPP TS 26.245](http://www.3gpp.org/ftp/Specs/html-info/26245.htm) specification to see what information is available. Note that you have to take care of the formatting and placement of the text yourself, the Flash Player will do nothing here. You can use [MP4Box](http://gpac.sourceforge.net/doc_ttxt.php) to inject text data into existing files.
  * Partial parsing support for the ['ilst' atom](http://atomicparsley.sourceforge.net/mpeg-4files.html) which is the ID3 equivalent iTunes uses to store meta data. This is usually present in iTunes files. It contains ID3 like information and is reported in the onMetaData callback as key/value pairs in a mixed array with the name 'tags'. ID3V2 is not supported right now.
  * A software based [H.264](http://en.wikipedia.org/wiki/H264) codec with the ability to decode Base, Mainline and High profiles.
  * An [AAC](http://en.wikipedia.org/wiki/Advanced_Audio_Coding) decoder supporting AAC Main, AAC LC and SBR (also known as HE-AAC ((The support of AAC allows you to encode audio to 64Kbit/s with the same quality of a 128Kbit/s encoded MP3. Further more, for other use more susceptible to bandwidth usage, like Internet Radio, HE-AAC v2 gives the possibility to encode audio to 32Kbit/s or lower with a surprisingly good final result. In low bitrate streaming scenarios this can make the difference.)).

## Issues ##
Tools to solve FLV-related issues:

  * [Utility for checking FLV format](http://labs.adobe.com/downloads/flvcheck.html)
  * [FLV Metadata Injector](http://www.buraks.com/flvmdi/) - free closed-source tool which can inject metadata information into a FLV file

## Video ##

### Overview ###
You load and play .mp4,.m4v,.m4a,.mov and .3gp files using the same NetStream API you use to load FLV files. There are a few things to be aware of:
  * Video needs to be in H.264 format only. MPEG-4 Part 2 (Xvid, DivX etc.) video is not supported, H.263 video is not supported, Sorenson Video is not supported. A lot of pod casts are still using MPEG-4 Part 2 so do not be surprised if you do not see any video.
  * the Flash Player is close to 100% compliant to the H.264 standard, all Base, Main, High and High 10 bit streams should play.
  * Extended, High 4:2:2 and High 4:4:4 profiles are not officially supported at this time. They might or might not work depending on what features are used. There are no artificial lower limit on B-frames or any problems with B-pyramids like other players do.
  * Since these files contain an index unlike old FLV files, the Flash Player provides a list of save seek points, e.g. times you can seek to without having the play head jump around. You'll get this information through the onMetaData callback in an array with the name 'seekpoints'. On the downside, some files are missing this information which also means that these files are not seekable at all! This is very different from the traditional FLV file format which is rather based on the notion of key frames to determine the seek points.

### Codecs ###

| Codec | Introduced in Flash Player version | Introduced in Flash Lite version | Container Formats | ISO Specification | Codec Id |
|:------|:-----------------------------------|:---------------------------------|:------------------|:------------------|:---------|
| [Sorenson Spark](http://en.wikipedia.org/wiki/Sorenson_codec) ((Flash documentation does not state a number for "their" version of Sorenson but describes the codec as a variant of [ITU-T](http://www.itu.int/ITU-T/) (International Telecommunications Union-Telecommunication Standardization Sector) recommendation [H.263](http://en.wikipedia.org/wiki/H.263) ([MPEG-4\_V](http://www.digitalpreservation.gov/formats/fdd/fdd000080.shtml)). In early 2006, one of Sorenson's compression applications to produce content for Flash offered the [Sorenson\_3](http://www.digitalpreservation.gov/formats/fdd/fdd000066.shtml) codec, described by experts as a variant of ITU-T H.264 ([MPEG-4\_AVC](http://www.digitalpreservation.gov/formats/fdd/fdd000081.shtml)). By late 2006, Sorenson offered new compression applications with other outputs.)) | 6 | 3 | FLV |  | 2 |
| Macromedia Screen Video ((This codec divides the screen in wide macroblocks (es: 64x64 pixels), reduces the number of colors, and transmits the changed blocks after compressing them in [zlib](http://en.wikipedia.org/wiki/Zlib). This is very similar to what VNC does.formats are bitmap tile based, can be lossy by reducing color depths and are compressed)) | 6 | - | FLV |  | 3 |
| Macromedia ScreenVideo 2 ((This codec can use two different types of macroblock: Iblock and Kblock. The Kblock works like a keyframe and is archived for future references. The Iblock is encoded as differences from a previous block. This new approach, similar to the usual compression of generic video content, guarantees a much better compression ratio, especially in a standard "moving windows" scenario.)) | 8 | - | FLV |  | 6 |
| [On2 TrueMotion VP6-E](http://en.wikipedia.org/wiki/VP6) | 8 | 3 | MOV |  | 4 |
| [On2 TrueMotion VP6-S](http://www.on2.com/index.php?358) | 9.0.115.0 | - | MP4V, M4V |  | 5 |
| [H.264](http://en.wikipedia.org/wiki/H.264) (MPEG-4 Part 10) | 9.0.115.0 | - | MP4, F4V, 3GP, 3G2 | [ISO 14496-10](http://www.iso.org/iso/en/CombinedQueryResult.CombinedQueryResult?queryString=14496-10) |  |


[Adobe Tech Note](http://kb.adobe.com/selfservice/viewContent.do?externalId=kb402866&sliceId=1)

## Audio ##

### Overview ###
  * Audio can be either [AAC](http://en.wikipedia.org/wiki/Advanced_Audio_Coding) Main, AAC LC or SBR, corresponding to audio object types 0, 1 and 2.
  * The '.mp3' sample type for tracks with mp3 audio is also supported.
  * MP3inMP4 which intends to do multi-channel mp3 playback within mp4 files is not supported.
  * The old QuickTime specific style of embedding AAC and MP3 data is not supported. It is unlikely though that you will run into these kind of files.
  * Unencrypted audio book files contain chapter information. This information is exposed in the onMetaData callback as an array of objects with name 'chapters'.
  * The Flash Player can play back multi-channel AAC files, though the sound is mixed down to two channels and resampled to 44.1Khz. Multi channel playback is targeted for one of the next major revisions of the Flash Player. This requires complete redesign of the sound engine in the Flash Player which dates from circa 1996 and has not been improved since.
  * All sampling rates from 8Khz to 96Khz are supported. A 32 tap [Kaiser Bessel](http://en.wikipedia.org/wiki/Kaiser_window) based FIR filter which resamples the sound to 44.1Khz, retaining high quality. The most common sample rate combinations have a hard coded number of phases. In case of a 48000 to 44100 Hz conversion the filter has 147 phases f.ex.
  * Flash Player Update 3 Beta 2 now can play back any MP3 sampling rate leveraging the same AAC implementation. No more chipmunks. Ever.



### Codecs ###

| Codec  | Introduced in Flash Player version | Container Formats | ISO Specification | Codec Id |
|:-------|:-----------------------------------|:------------------|:------------------|:---------|
| [MP3](http://en.wikipedia.org/wiki/MP3) | 6 | MP3 |  | 2 |
| [Nellymoser ASAO Codec](http://en.wikipedia.org/wiki/Nellymoser) (speech compression) audio content | 6 | FLV |  | 5, 6 |
| Raw [PCM](http://en.wikipedia.org/wiki/PCM) sampled audio content | 6 | WAV |  | 0 |
| [ADPCM](http://en.wikipedia.org/wiki/ADPCM) (Adaptive Delta Pulse Code Modulation) audio content | 6 |  |  | 1 |
| [AAC](http://en.wikipedia.org/wiki/Advanced_Audio_Coding) ([HE-AAC](http://en.wikipedia.org/wiki/HE-AAC)/AAC [SBR](http://en.wikipedia.org/wiki/Spectral_Band_Replication), AAC Main Profile, and AAC-LC) | 9.0.115.0 | M4A, MP4 | [ISO 14496-3](http://www.iso.org/iso/en/CombinedQueryResult.CombinedQueryResult?queryString=14496-3) |  |
| Speex | 10 | FLV | [Wiki](http://en.wikipedia.org/wiki/Speex) | 11 |

## Image ##

  * Image tracks encoded in JPEG, GIF and PNG are accessible in AS3 as a byte array through the callback 'onImageData'. You can simply take that byte array and use the Loader class to display the images. Most often these images represent cover artwork for audio files.
  * TIFF image tracks are not supported, you might come across files using this.
  * Support for the 'covr' meta data stored in iTunes files, accessible as byte arrays.

## Metadata ##
| Property | Value | Notes |
|:---------|:------|:------|
| duration | Obvious.  | Unlike for FLV files this field will always be present.  |
| videocodecid | For H.264 it reports 'avc1'.  |  |
| audiocodecid | For AAC it reports 'mp4a', for MP3 it reports '.mp3'.  |  |
| avcprofile | 66, 77, 88, 100, 110, 122 or 144  | Corresponds to the H.264 profiles  |
| avclevel | A number between 10 and 51.  | Consult [this](http://en.wikipedia.org/wiki/H264#Levels) list to find out more.  |
| aottype | Either 0, 1 or 2.  | This corresponds to AAC Main, AAC LC and SBR audio types.  |
| moovposition | int  | The offset in bytes of the [moov atom](http://developer.apple.com/documentation/QuickTime/QTFF/qtff.pdf) in a file.  |
| trackinfo | Array  | An array of objects containing various infomation about all the tracks in a file.  |
| chapters | Array  | Information about chapters in audiobooks.  |
| seekpoints | Array  | Times you can directly feed into NetStream.seek();  |
| videoframerate | int  | The frame rate of the video if a monotone frame rate is used. Most videos will have a monotone frame rate.  |
| audiosamplerate |  | The original sampling rate of the audio track.  |
| audiochannels |  | The original number of channels of the audio track.  |
| tags |  | ID3 like tag information  |

## FLV Format ##
A Flash Video file (.FLV file extension) consists of a short header, and then interleaved audio, video, and metadata packets.  The audio and video packets are stored very similarly to those in swf, and the metadata packets consist of AMF data.

### FLV Header ###

| Field             | Data Type     | Example                           | Description                                               |
|:------------------|:--------------|:----------------------------------|:----------------------------------------------------------|
| Signature         | byte[3](3.md)       | "FLV"                             | Always "FLV"                                              |
| Version           | uint8         | "\x01" (1)                        | Currently 1 for known FLV files                           |
| Flags             | uint8 bitmask | "\x05" (5, audio+video)           | Bitmask: 4 is audio, 1 is video                           |
| Offset            | uint32\_be     | "\x00\x00\x00\x09" (9)            | Total size of header (always 9 for known FLV files)       |


### FLV Stream ###

| Field             | Data Type     | Example                           | Description   |
|:------------------|:--------------|:----------------------------------|:--------------|
| PreviousTagSize   | uint32\_be     | "\x00\x00\x00\x00" (0)            | Always 0 |

Then a sequence of tags followed by their size until EOF.


### FLV Tag ###

| Field             	| Data Type     	| Example		| Description                                               			|
|:-------------------|:---------------|:---------|:-------------------------------------------------------------|
| Type              	| uint8         	| "\x12" (0x12, META)	| Determines the layout of Body, see below for tag types			|
| BodyLength        	| uint24\_be     	| "\x00\x00\xe0" (224)  | Size of Body (total tag size - 11)                        			|
| Timestamp         	| uint24\_be     	| "\x00\x00\x00" (0)	| Timestamp of tag (in milliseconds)                        			|
| TimestampExtended	| uint8     		| "\x00" (0) 		| Timestamp extension to form a uint32\_be. This field has the upper 8 bits. 	|
| StreamId 		| uint24\_be		| "\x00\x00\x00" (0)	| Always  0                                                  			|
| Body			| byte[BodyLength](BodyLength.md)	| ... 			| Dependent on the value of Type 						|

### Previous tag size ###
| Field             | Data Type     | Example                           | Description   |
|:------------------|:--------------|:----------------------------------|:--------------|
| PreviousTagSize   | uint32\_be     | "\x00\x00\x00\x00" (0)            | Total size of previous tag, or 0 for first tag|



### FLV Tag Types ###

| Tag code          | Name		| Description                                                                       		|
|:------------------|:------|:------------------------------------------------------------------------------------|
| 0x08              | AUDIO 		| Contains an audio packet similar to a SWF SoundStreamBlock plus codec information	|
| 0x09              | VIDEO 		| Contains a video packet similar to a SWF VideoFrame plus codec information        	|
| 0x12              | META 		| Contains two AMF packets, the name of the event and the data to go with it        	|



### FLV Tag 0x08: AUDIO ###

The first byte of an audio packet contains bitflags that
describe the codec used, with the following layout:

| Name          | Expression            | Description   |
|:--------------|:----------------------|:--------------|
| soundType     | (byte & 0x01) >> 0    | 0: mono, 1: stereo       |
| soundSize     | (byte & 0x02) >> 1    | 0: 8-bit, 1: 16-bit      |
| soundRate     | (byte & 0x0C) >> 2    | 0: 5.5 kHz (or speex 16kHz), 1: 11 kHz, 2: 22 kHz, 3: 44 kHz      |
| soundFormat   | (byte & 0xf0) >> 4    | 0: Uncompressed, 1: ADPCM, 2: MP3, 5: Nellymoser 8kHz mono, 6: Nellymoser, 11: Speex       |

The rest of the audio packet is simply the relevant data for that format, as per a SWF SoundStreamBlock.

### FLV Tag 0x09: VIDEO ###

The first byte of a video packet describes contains bitflags
that describe the codec used, and the type of frame

| Name          | Expression            | Description   |
|:--------------|:----------------------|:--------------|
| codecID       | (byte & 0x0f) >> 0    | 2: Sorensen H.263, 3: Screen video, 4: On2 VP6, 5: On2 VP6 Alpha, 6: ScreenVideo 2 |
| frameType     | (byte & 0xf0) >> 4    | 1: keyframe, 2: inter frame, 3: disposable inter frame |

In some cases it is also useful to decode some of the body of the video packet, such as to acquire its resolution (if the initial onMetaData META tag is missing, for example).


### FLV Tag 0x12: META ###

The contents of a meta packet are two AMF packets.  The first is almost always a short uint16\_be length-prefixed UTF-8 string
(AMF type 0x02), and the second is typically a mixed array (AMF type 0x08).  However, the second chunk typically contains
a variety of types, so a full AMF parser should be used.

## HTTP Streaming ##

It is possible to semi-stream flv over http using a trick which sends the normal headers then skips forward to a desired point in the file and moves the timestamps forward accordingly.

[A sample php script and fla is available at FlashComGuru](http://www.flashcomguru.com/index.cfm/2005/11/2/Streaming-flv-video-via-PHP-take-two)

Another tool that you can use to stream flv files using http is using [Flv4PHP](http://fanno.dk/index.php?option=com_content&task=blogcategory&id=15&Itemid=53) this tool is both a FLV Metadata injector and a stream tool, using php 4.x. this Project is GPL.