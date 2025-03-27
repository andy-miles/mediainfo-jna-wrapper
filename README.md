<a name="readme-top"></a>
<!-- Template Credit: Othneil Drew (https://github.com/othneildrew),
                      https://github.com/othneildrew/Best-README-Template/tree/master -->
<!-- PROJECT SHIELDS -->
<div align="center">

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

</div>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://mediaarea.net/en/MediaInfo">
    <img src="https://mediaarea.net/images/45c0bef-f798f49.png" alt="Logo" width="128" height="128">
  </a>
  <br/>
  <a href="https://mediaarea.net/en/MediaInfo">mediaarea.net</a>
  <h3 align="center">mediainfo-jna-wrapper</h3>

  <p align="center">
    A JNA wrapper to access MediaInfoLib.
    <br />
    <a href="https://www.amilesend.com/mediainfo-jna-wrapper"><strong>Maven Project Info</strong></a>
    -
    <a href="https://www.amilesend.com/mediainfo-jna-wrapper/apidocs/index.html"><strong>Javadoc</strong></a>
    <br />
    <a href="https://github.com/andy-miles/mediainfo-jna-wrapper/issues">Report Bug</a>
    -
    <a href="https://github.com/andy-miles/mediainfo-jna-wrapper/issues">Request Feature</a>
  </p>
</div>


<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#feature-highlights">Feature Highlights</a></li>
      </ul>
    </li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#recipes">Recipes</a></li>
      </ul>
    </li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>


<!-- ABOUT THE PROJECT -->
# About The Project

A JNA wrapper to access the native MediaInfo library to parse media information from media files.

<a name="feature-highlights"></a>
## Feature Highlights
1. JAR includes native libraries for MacOS (intel and arm), Linux (x64), and Windows (x64).
   1. Other MediaInfoLib platforms can still be used as long as the native libraries are included in the library path
3. Provides an abstract [MediaInfoBase](https://github.com/andy-miles/mediainfo-jna-wrapper/blob/main/src/main/java/com/amilesend/mediainfo/MediaInfoBase.java) class that can be extended to simplify access to media parameters.

<div align="right">(<a href="#readme-top">back to top</a>)</div>

<a name="getting-started"></a>
## Getting Started

Include this package as a dependency in your project. Note: This package is published to both
[GitHub](https://github.com/andy-miles/mediainfo-jna-wrapper/packages/2306114) and Maven Central repositories.

```xml
<dependency>
   <groupId>com.amilesend</groupId>
   <artifactId>mediainfo-jna-wrapper</artifactId>
   <version>1.0.6</version>
</dependency>
```

<div align="right">(<a href="#readme-top">back to top</a>)</div>

<a name="usage"></a>
# Usage
## Recipes
### Open a video file using the default MediaInfo object
```java
// Initialize the JNA bindings
MediaInfoLibrary library = MediaInfoLibrary.newInstance();
// The wrapper class to access the native methods
MediaInfoAccessor accessor = new MediaInfoAccessor(library);

// Opens a video file with the MediaInfo object being an AutoClosable
try (MediaInfo myVideo = new MediaInfo(accessor).open("./MyVideo.mkv")) {
    List<String> videoCodecs =
            MediaInfo.parseList(myVideo.get(StreamType.General, 0, "Video_Codec_List"));
    int videoWidth = Integer.parseInt(myVideo.get(StreamType.Video, 0, "Width"));
   
    // Get and parse additional parameters...
}
```
For more information, please refer to [javadoc](https://www.amilesend.com/mediainfo-jna-wrapper/apidocs/com/amilesend/mediainfo/MediaInfo.html) 
or [source](https://github.com/andy-miles/mediainfo-jna-wrapper/blob/main/src/main/java/com/amilesend/mediainfo/MediaInfo.java).

### Defining a custom MediaInfo object
```java
// Extend to define the explicit parameters for your java application.
public class MyVideoMediaInfo extends MediaInfoBase<MyVideoMediaInfo>  {
    public MyVideoMediaInfo(MediaInfoAccessor accessor) {
        super(accessor);
    }
 
    public List<String> getVideoCodecs() {
        String codecsList = mediaInfo.get(StreamType.General, 0, "Video_Codec_List");
        return parseList(codecsList);
    }

    public Duration getDuration() {
        long value = (long) Double.parseDouble(
                getAccessor().get(StreamType.General, 0, "Duration"));
        return Duration.ofMillis(value);
    }

    public int getWidth() {
        return Integer.parseInt(getAccessor().get(StreamType.Video, 0, "Width"));
    }

    public int getHeight() {
        return Integer.parseInt(getAccessor().get(StreamType.Video, 0, "Height"));
    }
    
    public Instant getCreationTime() {
        String encodingTime = getAccessor().get(StreamType.General, 0, "Encoded_Date");
        return parseTime(encodingTime);
    }
    
    // Define additional accessor methods here...
}

// Initialize the JNA bindings
MediaInfoLibrary library = MediaInfoLibrary.newInstance();
// The wrapper class to access the native methods
MediaInfoAccessor accessor = new MediaInfoAccessor(library);

// Opens a video file with the MyVideoMediaInfo object being an AutoClosable
try (MyVideoMediaInfo myVideo = new MyVideoMediaInfo(accessor).open("./MyVideo.mkv")) {
    List<String> videoCodecs = myVideo.getVideoCodecs();
    Duration movieDuration = myVideo.getDuration();
    int videoWidth = myVideo.getWidth();
    int videoHeight = myVideo.getHeight();
    Instant encodingTimestamp = myVideo.getCreationTime();

    // Access additional custom accessor methods...
}
```

<div align="right">(<a href="#readme-top">back to top</a>)</div>

<!-- CONTRIBUTING -->
## Contributing

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<div align="right">(<a href="#readme-top">back to top</a>)</div>

<!-- LICENSE -->
## License

Distributed under the MIT license. 

The built JAR also uses the MediaInfoLib Binary License for the bundled native libraries:
```
This product uses MediaInfo library, Copyright (c) 2002-2025 MediaArea.net SARL.
Website: https://mediaarea.net/en/MediaInfo
Email: info@mediaarea.net
```

See [LICENSE](https://github.com/andy-miles/mediainfo-jna-wrapper/blob/main/LICENSE) for more information.

<div align="right">(<a href="#readme-top">back to top</a>)</div>


<!-- CONTACT -->
## Contact

Andy Miles - andy.miles (at) amilesend.com

Project Link: [https://github.com/andy-miles/mediainfo-jna-wrapper](https://github.com/andy-miles/mediainfo-jna-wrapper)

<div align="right">(<a href="#readme-top">back to top</a>)</div>



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/andy-miles/mediainfo-jna-wrapper.svg?style=for-the-badge
[contributors-url]: https://github.com/andy-miles/mediainfo-jna-wrapper/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/andy-miles/mediainfo-jna-wrapper.svg?style=for-the-badge
[forks-url]: https://github.com/andy-miles/mediainfo-jna-wrapper/network/members
[stars-shield]: https://img.shields.io/github/stars/andy-miles/mediainfo-jna-wrapper.svg?style=for-the-badge
[stars-url]: https://github.com/andy-miles/mediainfo-jna-wrapper/stargazers
[issues-shield]: https://img.shields.io/github/issues/andy-miles/mediainfo-jna-wrapper.svg?style=for-the-badge
[issues-url]: https://github.com/andy-miles/mediainfo-jna-wrapper/issues
[license-shield]: https://img.shields.io/github/license/andy-miles/mediainfo-jna-wrapper.svg?style=for-the-badge
[license-url]: https://github.com/andy-miles/mediainfo-jna-wrapper/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/andy-miles
[product-screenshot]: images/screenshot.png
[Next.js]: https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white
[Next-url]: https://nextjs.org/
[React.js]: https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB
[React-url]: https://reactjs.org/
[Vue.js]: https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D
[Vue-url]: https://vuejs.org/
[Angular.io]: https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
[Svelte.dev]: https://img.shields.io/badge/Svelte-4A4A55?style=for-the-badge&logo=svelte&logoColor=FF3E00
[Svelte-url]: https://svelte.dev/
[Laravel.com]: https://img.shields.io/badge/Laravel-FF2D20?style=for-the-badge&logo=laravel&logoColor=white
[Laravel-url]: https://laravel.com
[Bootstrap.com]: https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery.com]: https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com 
