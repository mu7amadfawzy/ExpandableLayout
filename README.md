# ExpandableLayout

An Android library that lets you create an expandable layout in a simple and easy way in which you can use the default header and content OR pass your custom layout and just expand and collapse magic is all ready.

## Quick Setup

### 1- Include library

#### Using Gradle
```
dependencies {
implementation  'com.widget:expandableLayout:1+'
}
```
#### Using Maven
```
<dependency>
  <groupId>com.widget</groupId>
  <artifactId>expandableLayout</artifactId>
  <version>1+</version>
  <type>pom</type>
</dependency>

```
### 2- Usage

#### In XML Layout:
 
```
<widget.com.expandablelayout.ExpandableLayout
 android:layout_width="match_parent"
 android:layout_height="wrap_content"
 app:arrow_icon="@drawable/arrow_down"
 app:content_text="Content default Text Sample"
 app:content_color="@color/colorPrimaryDark"
 app:duration="300"
 app:header_title="Header default Text sample"
 app:header_color="@color/colorAccentDark" /> 

```
##### You can use the default HeaderTV and ContentTV

###### ````header_title```` sets the textColor of the headerTV
###### ````header_color```` sets the text of the headerTV 
###### ````arrow_icon```` sets the resource of the arrowBtn (which is visible with using the default headerTV) 

###### ````content_text````   sets the text of the contentTV
###### ````content_color```` sets the textColor of the contentTV

###### ````duration```` sets the duration of the collabse and expand animation

##### Or you can use set a custom header or a custom content 

###### ````header_layout````   sets the declared layout resource as the header layout
###### ````content_layout```` sets the declared layout resource as the content layout 


#### In Java:

````
ExpandableLayout expandableLayout=findViewById(R.id.expandableLayout);
````
##### Default HeaderTV and ContentTV
````
expandableLayout.setDefaultHeaderTitle("TITLE");
expandableLayout.setDefaultContentTitle("Content xxx");
expandableLayout.setArrowDrawable(R.drawable.arrow_ic);
````

##### Custom HeaderTV OR ContentTV
````
expandableLayout.setHeaderLayout(R.layout.custom_header);
expandableLayout.setContentLayout(R.layout.custom_content);
````

## Authors

* ***Initial work* - [Mohammed Fawzy](https://github.com/ma7madfawzy)
* ***Initial work* - [Ali Gamal](https://github.com/aligamal.dev)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

