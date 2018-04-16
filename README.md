# Empyr Android Library

[![CI Status](http://img.shields.io/travis/EmpyrNetwork/empyr_android.svg?style=flat)](https://travis-ci.org/EmpyrNetwork/empyr_android)
[![Download](https://api.bintray.com/packages/empyrnetwork/empyrnetwork/empyr_android/images/download.svg)](https://bintray.com/empyrnetwork/empyrnetwork/empyr_android/_latestVersion)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


Welcome to Empyr's Android Library. This library is intended to help ease the integration effort for partners that participate in the Empyr Offer Platform.

## Compatibility
The Empyr Android library is dependent upon the empyr_java library which has a dependency on the legacy commons httpclient. 

## Features
The Empyr iOS library is currently designed to support two primary feature sets:

- **API Integration** -- The Android SDK integrates the empyr_java library which provides wrappers around the Empyr API as well as model objects and serilization and deserialization components.
- **Tracker** -- The Tracker component is an easy way for publishers to send impression data to Empyr about their users viewing various offers. Additionally, this component provides Empyr with the data necessary to coordinate segmentation of those users which allows us to distribute more content to those users.

### Note
It should be noted that while both of the above features are available partners are not required to use **BOTH** of those functionalities.

<a name="installation"></a>
## Installation
Add the dependency to your build.gradle file.

```groovy
dependencies {
	implementation 'com.empyr:client:VERSION@aar' {
		transitive = true
	}
}
```

> Please note it is necessary to include the transitive dependencies to bring in the empyr_java library and it's dependencies.

<a name="integrate"></a>
## Integrate

To start the EmpyrAPIClient should be initialized in your MainActivity.

**MainActivity**
```java
package com.empyrdemo;  
  
import android.support.v7.app.AppCompatActivity;  
import android.os.Bundle;  
  
import com.empyr.api.EmpyrClient;  
import com.empyr.tracker.EmpyrTracker;  
  
public class MainActivity extends AppCompatActivity {  
  
    @Override  
  protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);  
  
	  EmpyrClient client = EmpyrClient.getInstance( "123345-01949949-029928818849938" );  
	  EmpyrTracker tracker = EmpyrTracker.getInstance( client, this );  
  
	  setContentView(R.layout.activity_main);  
  }  
}
```

### Identify your users
If a user has signed up for Empyr offers then the user should be identified with the Empyr SDK library. This can be accomplished through the "identify" function call.

```java
EmpyrClient.getInstance().identify( "partnerid@partner-empyr.com" );
```

## Tracker
> **WARNING** -- The Tracker component uses AAID. In order for the AAID to be properly resolved it is necessary to integrate [Google Play Services](https://developers.google.com/android/guides/setup). Note that the ads functionality of Google Play Services requires Android 2.3 or higher.

When an offer is being viewed by a user the EmpyrAPI should be notified. The offerId would be the offer that is being viewed by the user and **IS NOT** the business id but the actual offer id. For any given business if there is more than one offer then this would result in more than one call to the track function. Additionally, it is important to identify the type of impression (e.g. if it was a "profile" view or a "search" view).

```java
EmpyrTracker.getInstance().track( 12345, EmpyrTracker.Tracker.PROFILE_VIEW );  
EmpyrTracker.getInstance().track( 67890, EmpyrTracker.Tracker.PROFILE_VIEW );
```

## Example

To run the example project, clone the repo, and import into Android studio. A very simple example app is under the "app" directory.

## Author

Empyr Development, developer@empyr.com

## License

Empyr is available under the MIT license. See the LICENSE file for more info.
