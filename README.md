# Advanced CardView for Android
A CardView that simplifies implementing inner shadows, colorful drop shadows, gradients (specially sweep gradient), and more in Android.
With this library you can implement beautiful __NeoMorphism concept UI__ style into your app easily.
Minimum api is __Api 14+__ (Android 4.0.0).
Apache License 2.0

# Features
* Supports 2 Inner Shadows and 2 Outer (Drop) Shadows.
* Supports All gradients type for background and stroke.
* Supports Dashed style stroke
    * With different cap style support
* Supports Different Radius for Different Corners.

# Screens and Demos
With this library you can implement designs like images below very easily:

![Demo](/screens/screen_demo.png)

# Import
First add this line to your project gradle file:
```groovy
allprojects {
    repositories {
        // ..
        maven { url 'https://jitpack.io' }
    }
}
```
And you need to add gradle dependency:
```groovy
    implementation 'com.github.sina-seyfi:AdvancedCardView:1.0.0'
```

# Usage
__AdvancedCardView__ is a subclass of `FrameLayout` that can have multiple children base on your needs.

First you define view component in xml
```xml
<com.sinaseyfi.advancedcardview.AdvancedCardView
    android:id="@+id/advancedCardView"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

<!--    child views     -->

<com.sinaseyfi.advancedcardview.AdvancedCardView/>
```

And then you should define xml attributes base on your needs.
On upcoming paragraphs we explain these attributes.

#### `app:background_Type`
It has three different values:
* `fill` (__default__): View only has fill color.
* `stroke`: View only has stroke color.
* `fill_stroke`: View has both fill and stroke color.

__Caution:__ This property strictly define the view color behavior. When you define `background_Type` as __fill__ it doesn't matter if you define stroke color or width, it will be ignored.

#### `app:stroke_Width`
It's the width of stroke if you choose to have stroke. The default width is __1dp__.

#### `app:stroke_Type`
It works just like `background_Type` and will define the type of stroke.
It has two different values:
* `solid` (__default__): View stroke is solid.
* `dash`: View stroke is dashed.

__Caution:__ This property strictly define the stroke type.

#### `app:[background/stroke]_ColorType`
If you wants to implement gradients on background or stroke, you have to define __ColorType__ of them.
We have `app:background_ColorType` property for background and `app:stroke_ColorType` for stroke.
Both have these values (Except that __gradient_radial__ is not supported in stroke):
* `solid` (__default__): The color is solid.
* `gradient_linear`: The color is linear gradient.
* `gradient_radial`: The color is radial gradient (__Stroke DOES NOT support this one__).
* `gradient_sweep`: The color is sweep_gradient.

__Caution:__ These properties are strictly define color type.

#### `app:[background/stroke]_Color` and `app:[background/stroke]_Alpha`
if you define __solid__ as your background/stroke color type, the color of background/stroke will be determined by `app:[background/stroke]_Color` property.

Of course we have alpha for background/stroke: `app:[background/stroke]_Alpha`

__Remember:__ `app:[background/stroke]_Alpha` will affect on gradients too.
__Caution:__ If you defined alpha in your color hex, the library will multiply `app:[background/stroke]_Alpha` to your defined color hex alpha. So keep that in mind.

#### `app:[background/stroke]_Gradient_Color[index/End]`
If you chose one method of gradients as your background/stroke color type, you can define up to 8 colors as your gradient colors.
Properties that define the gradient colors are:
* `app:[background/stroke]_Gradient_Color0`
* `app:[background/stroke]_Gradient_Color1`
* `app:[background/stroke]_Gradient_Color2`
* `app:[background/stroke]_Gradient_Color3`
* `app:[background/stroke]_Gradient_Color4`
* `app:[background/stroke]_Gradient_Color5`
* `app:[background/stroke]_Gradient_Color6`
* `app:[background/stroke]_Gradient_ColorEnd`

You don't have to define all of these xml properties, just define as many as colors that you want on gradients in index order and last color on `app:[background/stroke]_Gradient_ColorEnd`.
__Caution:__ you have to define __ColorEnd__ of your gradient colors, otherwise the library will assume __Color0__ as you gradient's end color.

You can have infinite colors on your gradient if you define gradient colors programmatically:
```kotlin
acd.[background/stroke]_Gradient_Colors = colorArray
```

#### `app:[background/stroke_Gradient_Angle`
As the name explains, you can change gradient angle. Default angle is __0__ and it means upside down.
Angle range is from 0 to 360 and it turns clockwise.
You can see image below:
![Angle](/screens/angles.png)

__Remember:__ These angle rules, applies on all angle attributes that we'll see in upcoming document.

#### `app:[background/stroke]_Gradient_OffCenter_[X/Y]`
In both __gradient_radial__ and __gradient_sweep__ the center of gradient is the center of view. You can change gradient centers with __OffCenter_X__ or __OffCenter_Y__ multipliers. The range of these multiplier are from __-1__ to __+1__ and the default is __0__ that means don't move the center. For more information about how these multipliers works, see the image below:
![Off Center](/screens/offcenter.png)

#### `app:stroke_DashSize`, `app:stroke_GapSize` and `app:stroke_CapType`
If you choose to have dashed typed of stroke, you can define these attributes to change dashed behavior.
For more information see the image below:
![Dashed Stroke](/screens/dashed.png)

#### `app:cornerRadius`, `app:cornerRadius_TopLeft`, `app:cornerRadius_TopRight`, `app:cornerRadius_BottomRight` and `app:cornerRadius_BottomLeft`
These attributes, define corner radii. `app:cornerRadius` will change all corners simultaneously, but you can define other specific corners too.

__Point:__ The `app:cornerRadius` is low priority, that means if you defines this attribute and specificly define other corner radius like `app:cornerRadius_TopLeft`, top left corner radius __WILL NOT__ be determined by `app:cornerRadius` anymore. So if you want to have one corner different than others, you can define __cornerRadius__ and specifically tell library what corner you want to be different and you don't have to implement all corners individually. For example:
```xml
<com.sinaseyfi.advancedcardview.AdvancedCardView
    android:id="@+id/advancedCardView"
    android:layout_width="128dp"
    android:layout_height="128dp"
    app:background_Type="stroke"
    app:stroke_Color="#808080"
    app:cornerRadius="64dp"
    app:cornerRadius_TopRight="16dp">
```
Will have the result like this:

![Corner Radius Example](/screens/corner_radius_example.png)

#### `app:corner_Type`
It will automate and calculate the corner radius for you base of these values:
* `custom` (__default__): The library will take corner radii from you.
* `rectangular`: It will set corner radius to __0dp__ no matter if you defined relevant attributes.
* `circular`: It will make circular the edges of view. If you have view with equal width and height (sqauare), it will make it a circle. In this method, the library calculate minimum edge length of view and set corner radius to __half__ of it to make it cirle.
* `third`: It curves the corners, but with less tension than `circular`. Library calculate minimum edge length of view and set corner radius to __one third__ of it.
* `quarter`: Same as __third__ method, but library set the corner radius as __quarter__ of minimum edge length.

For more information about how these methods works, see the image below:
![Corner Type](/screens/corner_type.png)

#### `app:shadow[index]_[Inner/Outer]_[Blur/Distance/Alpha/Color/Angle]`
Finally we have attributes that define shadows for us. So if we want to define first inner shadow's blur by the naming convection, we will have attribute like this: `app:shadow0_Inner_Blur`.
Shadow Properties are described below:
* `Blur`: Defines blurriness of shadow.
* `Distance`: Defines distance of shadow.
* `Alpha`: Defines alpha of shadow.
* `Color`: Defines color of shadow.
* `Angle`: Defines angle of shadow.

__Remember:__ Angle behavior is just same as gradient angle that we discussed before.
__Caution:__ `Blur` attribute has to be defined, otherwise library don't draw related shadow.

#### `app:shadow_Outer_Area` and how library draw outer (drop) shadows
From api 26, android framework doesn't let the views to draw outside of their boundaries. For this problem to be fixed, outer shadows are part of view boundaries. This means that we have __inset__ or __padding__ if we draw outer shadows as the image below explains. The `app:shadow_Outer_Area` parameter defines how much inset we needs for outer shadows. This parameter will have default value of __8dp__ as soon as you declare first outer shadow, otherwise we don't have any inset.
When `app:shadow_Outer_Area` is defined, library automatically sets padding so the child views doesn't clip unnaturally.
![Shadow Outer Area](/screens/shadow_outer_area.png)

__Point:__ For most cases __8dp__ inset is enough, it means that if you have outer shadows' distance less than 8dp, view boundaries won't clip outer shadows. But if you have outer shadows' distance more than 8dp, change this attribute properly.
__Remember:__ This value is only matters for outer shadows not the inner shadows.
__Caution:__ Let the library automatically sets padding and don't sets padding manually. You can use `margin` for your view children to adjust their positions.

### Thank You for using my library.

