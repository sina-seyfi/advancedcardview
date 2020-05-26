package com.sinaseyfi.advancedcardview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout
import androidx.core.view.setPadding
import kotlin.math.*

class AdvancedCardView: FrameLayout {

    companion object {
        const val TAG = "ADVANCED_CARD_VIEW"
        val DEFAULT_BACKGROUND_COLOR_TYPE = ColorType.Solid
        val DEFAULT_BACKGROUND_TYPE = BackgroundType.Fill
        val DEFAULT_COLOR_BACKGROUND = Color.rgb( 255, 255, 255)
        val DEFAULT_COLOR_STROKE = Color.rgb(128, 128, 128)
        val DEFAULT_CORNER_RADIUS = 0f
        val DEFAULT_CORNER_TYPE = CornerType.Custom
        val DEFAULT_OFFCENTER_X = 0f
        val DEFAULT_OFFCENTER_Y = 0f
        val DEFAULT_RADIUS_MULTIPLIER = 1f
        val DEFAULT_SHADOW_COLOR = Color.rgb(0, 0, 0)
        val DEFAULT_STROKE_CAP_TYPE = CapType.Butt
        val DEFAULT_STROKE_COLOR_TYPE = ColorType.Solid
        val DEFAULT_STROKE_SIZE = 0f
        val DEFAULT_STROKE_TYPE = StrokeType.Solid
        val NOT_DEFINED_ALPHA = 1f
        val NOT_DEFINED_ANGLE = 0f
        val NOT_DEFINED_COLOR = -10
        val NOT_DEFINED_CORNER_RADIUS = Float.MIN_VALUE
        val NOT_DEFINED_DIMEN = -1f
        val NOT_DEFINED_RADIUS = -1f
        val NOT_DEFINED_SHADOW_OUTER_AREA = -1f
//        val NOT_DEFINED_SOURCE = -1       // TODO Implement Background Image Support
        val NOT_DEFINED_STYLE = -1
        val NOT_DEFINED_ANIMATE = false
        val DEFAULT_DURATION = 400L
    }

    // Constants that requires "resources"
    private val DEFAULT_SHADOW_OUTER_AREA = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
    private val DEFAULT_STROKE_DASH_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
    private val DEFAULT_STROKE_GAP_SIZE = DEFAULT_STROKE_DASH_SIZE
    private val SHADOW_OUTER_MASK_ERROR = 0.5f

    enum class BackgroundType {
        Fill,
        Stroke,
        Fill_Stroke
    }

    enum class StrokeType {
        Solid,
        Dash
    }

    enum class ColorType {
        Solid,
        Gradient_Linear,
        Gradient_Radial,
        Gradient_Sweep
    }

    enum class CapType {
        Square,
        Butt,
        Round
    }

    enum class CornerType {
        Custom,
        Rectangular,
        Circular,
        Third,
        Quarter
    }

    enum class ShadowType {
        Outer,
        Inner
    }

    private val DEBUG_MODE = true
    private val DEBUG_fill_color = Color.rgb(128, 128, 128)
    private val DEBUG_stroke_color = Color.rgb(0, 0, 255)
    private val DEBUG_stroke_width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    private val DEBUG_paint_fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val DEBUG_paint_stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    private val DEBUG_path = Path()

    // Background Fields
    var background_Type: BackgroundType = DEFAULT_BACKGROUND_TYPE
    var background_ColorType: ColorType = DEFAULT_BACKGROUND_COLOR_TYPE
    var background_Color: Int = DEFAULT_COLOR_BACKGROUND
    //    var background_Src = NOT_DEFINED_SOURCE
    private var background_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var background_Path = Path()
    var background_Alpha: Float = NOT_DEFINED_ALPHA
        set(value) {
            field = floatPercentCheck(value)
        }
    private lateinit var background_Path_Radii: FloatArray
    private var background_Path_Radii_Updated = false

    // Background Gradient Fields
    private var background_Gradient_Colors_Xml = IntArray(8, { NOT_DEFINED_COLOR })
    lateinit var background_Gradient_Colors: IntArray
    var background_Gradient_Angle: Float = NOT_DEFINED_ANGLE
        set(value) {
            field = angleCheck(value)
        }
    var background_Gradient_OffCenter_X = DEFAULT_OFFCENTER_X
        set(value) {
            field = boundaryCheck(value, 1f)
        }
    var background_Gradient_OffCenter_Y = DEFAULT_OFFCENTER_Y
        set(value) {
            field = boundaryCheck(value, 1f)
        }
    // TODO Implement Radius Multiplier
//    private var background_Gradient_Radius = NOT_DEFINED_RADIUS
//    private var background_Gradient_Radius_Multiplier = DEFAULT_RADIUS_MULTIPLIER

    // Stroke Fields
    var stroke_Type: StrokeType = DEFAULT_STROKE_TYPE
    var stroke_ColorType: ColorType = DEFAULT_STROKE_COLOR_TYPE
    var stroke_Color: Int = DEFAULT_COLOR_STROKE
    private var stroke_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var stroke_Mask = Path()
    private var stroke_Path = Path()
    private var no_Stroke_Path = Path()
    var stroke_Alpha: Float = NOT_DEFINED_ALPHA
        set(value) {
            field = floatPercentCheck(value)
        }
    private lateinit var stroke_Mask_Radii: FloatArray
    private var stroke_Mask_Radii_Updated = false
    private lateinit var stroke_Path_Radii: FloatArray
    private var stroke_Path_Radii_Updated = false

    var stroke_Width: Float = DEFAULT_STROKE_SIZE
        set(value) {
            field = dimenCheck(value, null)
        }
    var stroke_DashSize: Float = DEFAULT_STROKE_DASH_SIZE
        set(value) {
            field = dimenCheck(value, null)
        }
    var stroke_GapSize: Float = DEFAULT_STROKE_GAP_SIZE
        set(value) {
            field = dimenCheck(value, null)
        }
    var stroke_CapType: CapType = DEFAULT_STROKE_CAP_TYPE

    // Stroke Gradient Fields
    private var stroke_Gradient_Colors_Xml = IntArray(8, { NOT_DEFINED_COLOR })
    lateinit var stroke_Gradient_Colors: IntArray
    var stroke_Gradient_Angle = NOT_DEFINED_ANGLE
        set(value) {
            field = angleCheck(value)
        }

    var stroke_Gradient_OffCenter_X: Float = DEFAULT_OFFCENTER_X
        set(value) {
            field = boundaryCheck(value, 1f)
        }
    var stroke_Gradient_OffCenter_Y: Float = DEFAULT_OFFCENTER_Y
        set(value) {
            field = boundaryCheck(value, 1f)
        }

    private class ShadowObject {
        var shadowType: ShadowType
        var color = DEFAULT_SHADOW_COLOR
        var alpha = NOT_DEFINED_ALPHA
        var distance = NOT_DEFINED_DIMEN
        var blur = NOT_DEFINED_DIMEN
        var angle = NOT_DEFINED_ANGLE
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var path = Path()
        var mask = Path()
        constructor(shadowType: Int) {
            this.shadowType = ShadowType.values()[shadowType]
        }
    }

    // First Two Are Outer Shadows, And Last Two Are Inner Shadows
    private var shadows = Array<ShadowObject>(4) { ShadowObject((it / 2))}

    private lateinit var innerShadow_External_Path_Radii: FloatArray
    var shadow_Outer_Area: Float = NOT_DEFINED_SHADOW_OUTER_AREA
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_SHADOW_OUTER_AREA)
            initPadding()
        }

    // Corner Fields
    var cornerRadius_: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerRadius_TopLeft: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerRadius_TopRight: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerRadius_BottomLeft: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerRadius_BottomRight: Float = NOT_DEFINED_CORNER_RADIUS
        set(value) {
            field = dimenCheck(value, NOT_DEFINED_CORNER_RADIUS)
            cornerUpdated()
        }
    var cornerType = DEFAULT_CORNER_TYPE
        set(value) {
            field = value
            cornerUpdated()
        }

    var onTouched_Style = NOT_DEFINED_STYLE
    var onTouched_Animate = NOT_DEFINED_ANIMATE
    var onTouched_Duration = DEFAULT_DURATION

    constructor(context: Context) : super(context) { initialize(context, null) }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize(context, attrs) }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initialize(context, attrs) }

    // *
    // *
    // *
    // Preparing Area

    private fun initialize(context: Context, attrs: AttributeSet?) {
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.AdvancedCardView)
            // Get Background Style from XML
            background_Type = BackgroundType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_background_Type, DEFAULT_BACKGROUND_TYPE.ordinal)]
            background_ColorType = ColorType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_background_ColorType, DEFAULT_BACKGROUND_COLOR_TYPE.ordinal)]
            background_Color = typedArray.getColor(R.styleable.AdvancedCardView_background_Color, DEFAULT_COLOR_BACKGROUND)
            background_Alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Alpha, NOT_DEFINED_ALPHA))
            background_Gradient_Colors_Xml[0] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color0, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[1] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color1, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[2] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color2, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[3] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color3, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[4] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color4, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[5] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color5, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[6] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_Color6, NOT_DEFINED_COLOR)
            background_Gradient_Colors_Xml[7] = typedArray.getColor(R.styleable.AdvancedCardView_background_Gradient_ColorEnd, NOT_DEFINED_COLOR)
            background_Gradient_Angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Gradient_Angle, NOT_DEFINED_ANGLE))
            background_Gradient_OffCenter_X = boundaryCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Gradient_OffCenter_X, DEFAULT_OFFCENTER_X), 1f)
            background_Gradient_OffCenter_Y = boundaryCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Gradient_OffCenter_Y, DEFAULT_OFFCENTER_Y), 1f)
//            background_Gradient_Radius = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_background_Gradient_Radius, NOT_DEFINED_RADIUS))
//            background_Gradient_Radius_Multiplier = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_background_Gradient_Radius_Multiplier, DEFAULT_RADIUS_MULTIPLIER))
            // Get Stroke Style from XML
            stroke_Type = StrokeType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_stroke_Type, DEFAULT_STROKE_TYPE.ordinal)]
            stroke_ColorType = ColorType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_stroke_ColorType, DEFAULT_STROKE_COLOR_TYPE.ordinal)]
            stroke_Color = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Color, DEFAULT_COLOR_STROKE)
            stroke_Alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_stroke_Alpha, NOT_DEFINED_ALPHA))
            stroke_Width = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_stroke_Width, DEFAULT_STROKE_SIZE), null)
            stroke_Gradient_Colors_Xml[0] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color0, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[1] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color1, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[2] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color2, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[3] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color3, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[4] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color4, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[5] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color5, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[6] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_Color6, NOT_DEFINED_COLOR)
            stroke_Gradient_Colors_Xml[7] = typedArray.getColor(R.styleable.AdvancedCardView_stroke_Gradient_ColorEnd, NOT_DEFINED_COLOR)
            stroke_Gradient_Angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_stroke_Gradient_Angle, NOT_DEFINED_ANGLE))
            stroke_Gradient_OffCenter_X = boundaryCheck(typedArray.getFloat(R.styleable.AdvancedCardView_stroke_Gradient_OffCenter_X, DEFAULT_OFFCENTER_X), 1f)
            stroke_Gradient_OffCenter_Y = boundaryCheck(typedArray.getFloat(R.styleable.AdvancedCardView_stroke_Gradient_OffCenter_Y, DEFAULT_OFFCENTER_Y), 1f)
            stroke_DashSize = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_stroke_DashSize, DEFAULT_STROKE_DASH_SIZE), null)
            stroke_GapSize = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_stroke_GapSize, DEFAULT_STROKE_GAP_SIZE), null)
            stroke_CapType = CapType.values()[(typedArray.getInteger(R.styleable.AdvancedCardView_stroke_CapType, DEFAULT_STROKE_CAP_TYPE.ordinal))]
            // Get Shadow Data from XML
            // Outer Shadow 1
            shadows[0].color = typedArray.getColor(R.styleable.AdvancedCardView_shadow0_Outer_Color, DEFAULT_SHADOW_COLOR)
            shadows[0].alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow0_Outer_Alpha, NOT_DEFINED_ALPHA))
            shadows[0].distance = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow0_Outer_Distance, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[0].blur = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow0_Outer_Blur, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[0].angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow0_Outer_Angle, NOT_DEFINED_ANGLE))
            // Outer Shadow 2
            shadows[1].color = typedArray.getColor(R.styleable.AdvancedCardView_shadow1_Outer_Color, DEFAULT_SHADOW_COLOR)
            shadows[1].alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow1_Outer_Alpha, NOT_DEFINED_ALPHA))
            shadows[1].distance = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow1_Outer_Distance, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[1].blur = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow1_Outer_Blur, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[1].angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow1_Outer_Angle, NOT_DEFINED_ANGLE))
            // Inner Shadow 1
            shadows[2].color = typedArray.getColor(R.styleable.AdvancedCardView_shadow0_Inner_Color, DEFAULT_SHADOW_COLOR)
            shadows[2].alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow0_Inner_Alpha, NOT_DEFINED_ALPHA))
            shadows[2].distance = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow0_Inner_Distance, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[2].blur = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow0_Inner_Blur, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[2].angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow0_Inner_Angle, NOT_DEFINED_ANGLE))
            // Inner Shadow 2
            shadows[3].color = typedArray.getColor(R.styleable.AdvancedCardView_shadow1_Inner_Color, DEFAULT_SHADOW_COLOR)
            shadows[3].alpha = floatPercentCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow1_Inner_Alpha, NOT_DEFINED_ALPHA))
            shadows[3].distance = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow1_Inner_Distance, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[3].blur = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow1_Inner_Blur, NOT_DEFINED_DIMEN), NOT_DEFINED_DIMEN)
            shadows[3].angle = angleCheck(typedArray.getFloat(R.styleable.AdvancedCardView_shadow1_Inner_Angle, NOT_DEFINED_ANGLE))
            //
            shadow_Outer_Area = dimenCheck(typedArray.getDimension(R.styleable.AdvancedCardView_shadow_Outer_Area, NOT_DEFINED_SHADOW_OUTER_AREA), NOT_DEFINED_SHADOW_OUTER_AREA)
            //
            // Get Corner Radius from XML
            cornerRadius_ = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius, NOT_DEFINED_CORNER_RADIUS)
            cornerRadius_TopLeft = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius_TopLeft, NOT_DEFINED_CORNER_RADIUS)
            cornerRadius_TopRight = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius_TopRight, NOT_DEFINED_CORNER_RADIUS)
            cornerRadius_BottomRight = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius_BottomRight, NOT_DEFINED_CORNER_RADIUS)
            cornerRadius_BottomLeft = typedArray.getDimension(R.styleable.AdvancedCardView_cornerRadius_BottomLeft, NOT_DEFINED_CORNER_RADIUS)
            cornerType = CornerType.values()[typedArray.getInteger(R.styleable.AdvancedCardView_corner_Type, DEFAULT_CORNER_TYPE.ordinal)]
            typedArray.recycle()
        }

        setWillNotDraw(false)
        clipToPadding = false
        initLayerTypes(background_Paint, stroke_Paint, shadows[0].paint, shadows[1].paint, shadows[2].paint, shadows[3].paint)
        initShadowOuterArea()
        initPadding()
        initBackgroundGradientColors()
        initStrokeGradientColors()

    }

    private fun initDebugFields() {
        DEBUG_paint_fill.style = Paint.Style.FILL
        DEBUG_paint_stroke.style = Paint.Style.STROKE
        DEBUG_paint_fill.color = DEBUG_fill_color
        DEBUG_paint_stroke.color = DEBUG_stroke_color
        DEBUG_paint_stroke.strokeWidth = DEBUG_stroke_width
        DEBUG_path.reset()
        DEBUG_path.addRect(0 + DEBUG_stroke_width / 2, 0 + DEBUG_stroke_width / 2, measuredWidth - DEBUG_stroke_width / 2, measuredHeight - DEBUG_stroke_width / 2, Path.Direction.CW)
    }

    private fun initShadowOuterArea() {
        if(shadow_Outer_Area == NOT_DEFINED_SHADOW_OUTER_AREA)
            if(haveOuterShadow() && shadow_Outer_Area != 0f)
                shadow_Outer_Area = DEFAULT_SHADOW_OUTER_AREA
    }

    private fun initPadding() {
        setPadding(getShadowOuterArea().toInt())
    }

    private fun initLayerTypes(vararg paints: Paint) {
        for(p in paints)
            if(android.os.Build.VERSION.SDK_INT < 28)
                setLayerType(View.LAYER_TYPE_SOFTWARE, p)
            else
                setLayerType(View.LAYER_TYPE_HARDWARE, p)
    }

    private fun initShadow(shadow: ShadowObject) {
        if(shadow.shadowType == ShadowType.Outer) {
            initShadowOuterPaint(shadow)
            initShadowOuterPath(shadow)
            initShadowOuterMask(shadow)
        } else if(shadow.shadowType == ShadowType.Inner) {
            initShadowInnerPaint(shadow)
            initShadowInnerPath(shadow)
            initShadowInnerMask(shadow)
        }
    }

    private fun initShadowOuterPaint(shadow: ShadowObject) {
        shadow.paint.style = Paint.Style.FILL
        shadow.paint.color = Color.rgb(128, 128, 128)
        shadow.paint.setShadowLayer(shadow.blur, getDx(shadow.distance, -shadow.angle), getDy(shadow.distance, -shadow.angle), assignColorAlpha(shadow.color, shadow.alpha))
    }

    private fun initShadowOuterPath(shadow: ShadowObject) {
        shadow.path.reset()
        addBackgroundRectF(shadow.path)
    }

    private fun initShadowOuterMask(shadow: ShadowObject) {
        shadow.mask.reset()
        addBoundaryRectF(shadow.mask)
//        addBackgroundRectF(shadow.mask)
        addBackgroundOuterShadowMaskRectF(shadow.mask)
        shadow.mask.fillType = Path.FillType.EVEN_ODD
    }

    // Init shadow inner paint.
    private fun initShadowInnerPaint(shadow: ShadowObject) {
        shadow.paint.style = Paint.Style.FILL
        shadow.paint.color = Color.rgb(255, 255, 255)
        shadow.paint.setShadowLayer(shadow.blur, getDx(shadow.distance, -shadow.angle), getDy(shadow.distance, -shadow.angle), assignColorAlpha(shadow.color, shadow.alpha))
    }

    // Init shadow inner path.
    // We add external rectF and masking it with no stroke area rectF.
    private fun initShadowInnerPath(shadow: ShadowObject) {
        shadow.path.reset()
        addInnerShadowExternalRectF(shadow.path, -shadow.blur * 4)
        addNoStrokeAreaRectF(shadow.path)
        shadow.path.fillType = Path.FillType.EVEN_ODD   // Even_Odd will cut out second added path.
    }

    // Init shadow inner mask.
    // For this mask we need no stroke area rectF.
    private fun initShadowInnerMask(shadow: ShadowObject) {
        shadow.mask.reset()
        addNoStrokeAreaRectF(shadow.mask)
    }

    // Init background paint, style is fill.
    private fun initBackgroundPaint() {
        background_Paint.style = Paint.Style.FILL
        background_Paint.alpha = mapAlphaTo255(background_Alpha)
        background_Paint.color = background_Color
        when(background_ColorType) {
            ColorType.Gradient_Linear -> background_Paint.shader = getLinearShader(
                background_Gradient_Colors,
                background_Gradient_Angle
            )
            ColorType.Gradient_Radial -> background_Paint.shader = getRadialShader(
                background_Gradient_Colors,
                background_Gradient_OffCenter_X,
                background_Gradient_OffCenter_Y
            )
            ColorType.Gradient_Sweep -> background_Paint.shader = getSweepShader(
                background_Gradient_Colors,
                background_Gradient_Angle,
                background_Gradient_OffCenter_X,
                background_Gradient_OffCenter_Y
            )
        }
    }

    // Init stroke paint for dashed and non-dashed stroke path effect.
    // If path effect is dashed, style will be stroke.
    // else, style will be fill and we will use mask.
    private fun initStrokePaint() {
        if(isDashed()) {
            stroke_Paint.style = Paint.Style.STROKE
            stroke_Paint.pathEffect = DashPathEffect(floatArrayOf(stroke_DashSize, stroke_GapSize), 0f)
            stroke_Paint.strokeCap = getStrokeCap(stroke_CapType)
            stroke_Paint.strokeWidth = getStrokeWidth()
        } else {
            stroke_Paint.style = Paint.Style.FILL
            stroke_Paint.alpha = mapAlphaTo255(stroke_Alpha)
            stroke_Paint.color = stroke_Color
        }
        when(stroke_ColorType) {
            ColorType.Gradient_Linear -> stroke_Paint.shader = getLinearShader(
                stroke_Gradient_Colors,
                stroke_Gradient_Angle
            )
            ColorType.Gradient_Sweep -> stroke_Paint.shader = getSweepShader(
                stroke_Gradient_Colors,
                stroke_Gradient_Angle,
                stroke_Gradient_OffCenter_X,
                stroke_Gradient_OffCenter_Y
            )
        }
    }

    // Init inner shadow external rectF radii
    private fun initInnerShadowExternalPathRadii(inset: Float) {
        innerShadow_External_Path_Radii = getCornerRadii(
            getCornerRadius(),
            cornerRadius_TopLeft,
            cornerRadius_TopRight,
            cornerRadius_BottomRight,
            cornerRadius_BottomLeft,
            // Inset is always negative
            // and is a factor of inner shadow blur.
            getStrokeWidth() + inset
        )
    }

    // Init stroke path radii (to be used in non-dashed style)
    private fun initStrokeMaskRadii() {
        if(!stroke_Mask_Radii_Updated) {
            stroke_Mask_Radii = getCornerRadii(
                getCornerRadius(),
                cornerRadius_TopLeft,
                cornerRadius_TopRight,
                cornerRadius_BottomRight,
                cornerRadius_BottomLeft,
                // We ignore stroke part, so we inset all stroke width
                // Because we want to use this as cut out.
                getStrokeWidth()
            )
            stroke_Mask_Radii_Updated = true
        }
    }

    // Init stroke path radii (to be used in dashed style)
    private fun initStrokePathRadii() {
        if(!stroke_Path_Radii_Updated) {
            stroke_Path_Radii = getCornerRadii(
                getCornerRadius(),
                cornerRadius_TopLeft,
                cornerRadius_TopRight,
                cornerRadius_BottomRight,
                cornerRadius_BottomLeft,
                // Half of stroke width for inset because stroke is drawn on path
                getStrokeWidth() / 2
            )
            stroke_Path_Radii_Updated = true
        }
    }

    // Init background corner radii.
    private fun initBackgroundCornerRadii() {
        if(!background_Path_Radii_Updated) {
            background_Path_Radii = getCornerRadii(
                getCornerRadius(),
                cornerRadius_TopLeft,
                cornerRadius_TopRight,
                cornerRadius_BottomRight,
                cornerRadius_BottomLeft,
                // We don't need any inset
                0f
            )
            background_Path_Radii_Updated = true
        }
    }

    // Init background path
    // Adds background rectF.
    private fun initBackgroundPath() {
        background_Path.reset()
        addBackgroundRectF(background_Path)
    }

    // Init stroke path for dashed stroke style.
    // Adds stroke path rectF.
    private fun initStrokePath() {
        stroke_Path.reset()
        addStrokePath(stroke_Path)
    }

    // Init stroke mask for non-dashed stroke style.
    // Adds background rectF and cuts it by no stroke rectF.
    private fun initStrokeMask() {
        stroke_Mask.reset()
        addBackgroundRectF(stroke_Mask)
        addNoStrokeAreaRectF(stroke_Mask)
        stroke_Mask.fillType = Path.FillType.EVEN_ODD
    }

    // Init no stroke area path.
    // Will be used to clip children of this container.
    private fun initNoStrokePath() {
        no_Stroke_Path.reset()
        addNoStrokeAreaRectF(no_Stroke_Path)
    }

    // Assign alpha to background gradient colors
    private fun initBackgroundGradientColors() {
        background_Gradient_Colors = getColorArray(background_Gradient_Colors_Xml, background_Alpha)
    }

    // Assign alpha to stroke gradient colors
    private fun initStrokeGradientColors() {
        stroke_Gradient_Colors = getColorArray(stroke_Gradient_Colors_Xml, stroke_Alpha)
    }

    // Assign inset to corner radii.
    // Positive inset will make corners less curve (because boundaries are smaller)
    // Negative inset will make corners more curve (because boundaries are larger)
    // Also check if individual corner radius is set.
    private fun getCornerRadii(radius: Float, topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float, inset: Float): FloatArray {
        val cornerRadius = if(radius == NOT_DEFINED_CORNER_RADIUS) DEFAULT_CORNER_RADIUS else dimenCheck(radius - inset, null)
        val cornerRadiusTopLeft = if(topLeft == NOT_DEFINED_CORNER_RADIUS) cornerRadius else dimenCheck(topLeft - inset, null)
        val cornerRadiusTopRight = if(topRight == NOT_DEFINED_CORNER_RADIUS) cornerRadius else dimenCheck(topRight - inset, null)
        val cornerRadiusBottomRight = if(bottomRight == NOT_DEFINED_CORNER_RADIUS) cornerRadius else dimenCheck(bottomRight - inset, null)
        val cornerRadiusBottomLeft = if(bottomLeft == NOT_DEFINED_CORNER_RADIUS) cornerRadius else dimenCheck(bottomLeft - inset, null)
        return floatArrayOf(
            cornerRadiusTopLeft, cornerRadiusTopLeft,
            cornerRadiusTopRight, cornerRadiusTopRight,
            cornerRadiusBottomRight, cornerRadiusBottomRight,
            cornerRadiusBottomLeft, cornerRadiusBottomLeft
        )
    }

    // Normalize color array that is send from xml,
    // It will append color in series, for example if
    // only colors of 3, 5, 6 is provided, it will map
    // these color indexes, to 0, 1, 2.
    // It will append first color in array, if last color is not defined.
    // It will set default color values, if no colors are defined.
    private fun getColorArray(colors: IntArray, alpha: Float): IntArray {
        var colorArray = ArrayList<Int>()
        for(c in colors)
            if(c != NOT_DEFINED_COLOR)
                colorArray.add(
                    assignColorAlpha(c, alpha)
                )
        if(colors.last() == NOT_DEFINED_COLOR && colorArray.size != 0)
            colorArray.add(colorArray.first())
        else if(colorArray.size == 0) {
            colorArray.add(DEFAULT_COLOR_STROKE)
            colorArray.add(DEFAULT_COLOR_STROKE)
        }
        return colorArray.toIntArray()
    }

    // Assign alpha to color even if the color has its own alpha.
    // For example if the color has alpha of 0.5, assigning the
    // alpha of 0.5 to it, will make the result color alpha to be 0.5 * 0.5 = 0.25
    private fun assignColorAlpha(color: Int, alpha: Float): Int {
        return Color.argb(
            mapAlphaTo255(
                alpha * mapAlphaTo1(Color.alpha(color))
            ),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    // It automates corner radius.
    private fun getCornerRadius(): Float {
        return when(cornerType) {
            CornerType.Custom -> cornerRadius_
            CornerType.Rectangular -> 0f
            CornerType.Circular -> min(getActualWidth(), getActualHeight()) / 2
            CornerType.Third -> min(getActualWidth(), getActualHeight()) / 3
            CornerType.Quarter -> min(getActualWidth(), getActualHeight()) / 4
        }
    }

    // Check dimen to be not negative.
    // Allowed dimen is used when we want dimen to be negative (For example, when we not set it)
    private fun dimenCheck(dimen: Float, allowedDimen: Float?): Float {
        if(allowedDimen != null)
            if(dimen == allowedDimen)
                return dimen
        return if(dimen >= 0f)
            dimen
        else
            0f
    }

    // Helper function that calculates left, top, right, bottom values base on inputs
    // and creates the RectF
    private fun getRectF(width: Float, height: Float, inset: Float): RectF {
        return RectF(0f + inset, 0f + inset, width - inset, height - inset)
    }

    // Create inset background path rectF
    private fun addBackgroundRectF(path: Path) {
        path.addRoundRect(
            getRectF(
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                getShadowOuterArea()
            ),
            getBackgroundPathRadii(),
            Path.Direction.CW
        )
    }

    // Outer shadow is drawn between actual boundary (view boundary)
    // and the inset boundary. So if we have stroke type background color,
    // we don't want outer shadow to draw inside stroked path (because path is a cut out path)
    // so we need to mask outer shadow drawing to only draw outside inset boundary:
    private fun addBackgroundOuterShadowMaskRectF(path: Path) {
        path.addRoundRect(
            getRectF(
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                getShadowOuterArea() - SHADOW_OUTER_MASK_ERROR
            ),
            getBackgroundPathRadii(),
            Path.Direction.CW
        )
    }

    // This function is used when we have dashed type stroke.
    // As the stroke type drawing, draws stroke on path,
    // So we need to create rectF smaller by half of stroke width.
    private fun addStrokePath(path: Path) {
        path.addRoundRect(
            getRectF(
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                getShadowOuterArea() + getStrokeWidth() / 2     // because drawing is on path.
            ),
            getStrokePathRadii(),
            Path.Direction.CW
        )
    }

    // Adds cut out rectF that is not include any stroke inside.
    // This function will be used by stroke path initializer,
    // and by inner shadow path initializer.
    // both of these function, will use this rectF as cut out.
    private fun addNoStrokeAreaRectF(path: Path) {
        path.addRoundRect(
            getRectF(
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                getShadowOuterArea() + getStrokeWidth()
            ),
            getStrokeMaskRadii(),
            Path.Direction.CW
        )
    }

    // Adds boundary rectF without considering corner radii:
    private fun addBoundaryRectF(path: Path) {
        path.addRect(
            getRectF(
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                0f),
            Path.Direction.CW
        )
    }

    // Inner shadow is a big rectF (even bigger than boundary rectF, if blur is too high)
    // that the inside is cut out and empty, so when we draw outer shadow,
    // it seems that it is a inner shadow. This function creates bigger and outer rectF:
    private fun addInnerShadowExternalRectF(path: Path, expand: Float) {
        path.addRoundRect(
            getRectF(
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                getShadowOuterArea() + expand
            ),
            getInnerShadowExternalPathRadii(expand),
            Path.Direction.CW
        )
    }

    // Map angle to 0 - 360 range:
    private fun angleCheck(angle: Float): Float {
        return if(angle < 0)
            angleCheck(angle + 360)
        else
            angle % 360
    }

    // Checks if a float number is in 0 - 1 range and clamps it:
    private fun floatPercentCheck(alpha: Float): Float {
        return when {
            alpha < 0f -> 0f
            alpha > 1f -> 1f
            else -> alpha
        }
    }

    // Maps alpha to 0 - 255 range:
    private fun mapAlphaTo255(alpha: Float): Int {
        return (floatPercentCheck(alpha) * 255).toInt()
    }

    // Maps alpha to 0 - 1 range:
    private fun mapAlphaTo1(alpha: Int): Float {
        return alpha / 255f
    }

    // Converts angle to radian:
    private fun angleToRadians(angle: Float): Double {
        return Math.toRadians(angle.toDouble())
    }

    // Calculates dx base on angle and radius:
    private fun getDx(radius: Float, angle: Float): Float {
        return (radius * sin(angleToRadians(angle))).toFloat()
    }

    // Calculates dy base on angle and radius:
    private fun getDy(radius: Float, angle: Float): Float {
        return (radius * cos(angleToRadians(angle))).toFloat()
    }

    // Calculates diameter:
    private fun getDiameter(x: Float, y: Float): Float {
        return sqrt(x.pow(2) + y.pow(2))
    }

    // Checks if a value is outside of both positive and negative boundary:
    private fun boundaryCheck(value: Float, boundary: Float): Float {
        val absValue = abs(value)
        val sign = (value / absValue).toInt()
        return if(absValue >= boundary) sign * boundary
        else sign * absValue
    }

    // TODO( Create Rounded Sweep and Sudden Sweep Mode )
    // Creates sweep shader:
    private fun getSweepShader(colorArray: IntArray, angle: Float, offCenterX: Float, offCenterY: Float): SweepGradient {
        val sweepGradient = SweepGradient(getCenterX(offCenterX), getCenterY(offCenterY), colorArray, null)
        val matrix = Matrix()
        matrix.postRotate(angle - 90, getCenterX(offCenterX), getCenterY(offCenterY))
        sweepGradient.setLocalMatrix(matrix)
        return sweepGradient
    }

    // Creates radial shader:
    private fun getRadialShader(colorArray: IntArray, offCenterX: Float, offCenterY: Float): RadialGradient {
        return RadialGradient(getCenterX(offCenterX), getCenterY(offCenterY), getShaderRadius(offCenterX, offCenterY), colorArray, null, Shader.TileMode.CLAMP)
    }

    // Calculate offCenter value of X axis base on multiplier:
    private fun getCenterX(offCenter: Float): Float {
        val halfWidth = getActualWidth() / 2 + getShadowOuterArea()
        return halfWidth + offCenter * halfWidth
    }

    // Calculate offCenter value of Y axis base on multiplier:
    private fun getCenterY(offCenter: Float): Float {
        val halfHeight = getActualHeight() / 2 + getShadowOuterArea()
        return halfHeight + offCenter * halfHeight
    }

    // Calculate how much radial shader radius must be,
    // to fill out entire boundary:
    private fun getShaderRadius(offCenterX: Float, offCenterY: Float): Float {
        val offCenter = getDiameter(offCenterX, offCenterY)
        val halfDiameter = getDiameter(getActualWidth(), getActualHeight()) / 2
        return halfDiameter + offCenter * halfDiameter
    }

    // Creating linear shader that start and end point is on path:
    private fun getLinearShader(colorArray: IntArray, angle: Float): LinearGradient {
        val startPoint = getLinearGradientCircularStartPoint(angle)
        val endPoint = getLinearGradientCircularStartPoint(angle + 180)
        return LinearGradient(startPoint.first, startPoint.second, endPoint.first, endPoint.second, colorArray, null, Shader.TileMode.CLAMP)
    }

    // Calculate position of linear gradient starting point,
    // with considering each corner radius of shape.
    // In this approach starting point of linear gradient,
    // will fall on the path even on the curved corners.
    // So when we have multiple colors in linear gradient,
    // no colors will fall outside of path when corner is curved.
    private fun getLinearGradientCircularStartPoint(angle: Float): Pair<Float, Float> {
        val width = getActualWidth()
        val height = getActualHeight()
        // These values will be used to calculate diameters:
        val halfWidth = width / 2
        val halfHeight = height / 2
        // We need half diameter because we move from center of both X and Y axises:
        val halfDiameter = getDiameter(width, height) / 2
        // Now we calculate how much we need to move from center.
        val dx = getDx(halfDiameter, angle)
        val dy = getDy(halfDiameter, angle)
        // Because dx and dy are calculated by half diameter,
        // if we don't have square, dx and dy will be,
        // more than half width/height in some conditions,
        // so we need to clamp values to maximum of half width/height:
        var x = halfWidth + boundaryCheck(dx, halfWidth)
        var y = halfHeight + boundaryCheck(-dy, halfHeight) // -dy because y axis in android is upside down.
        // Now We Make Coordinates of X and Y Circular Base on Corner Radius:
        val pathRadii = getBackgroundPathRadii()
        val CTR = pathRadii[2]      // Corner Top Right
        val CBR = pathRadii[4]      // Corner Bottom Right
        val CBL = pathRadii[6]      // Corner Bottom Left
        val CTL = pathRadii[0]      // Corner Top Left
        var circularAngle: Float
        if(x >= width - CTR && y <= CTR) {
            // Top Right
            circularAngle = determineCornerCircularAngle(x - (width - CTR), y, CTR)
            // No Addition
            x = width - CTR + getDx(CTR, circularAngle)
            y = CTR - getDy(CTR, circularAngle)
        } else if(x >= width - CBR && y >= height - CBR) {
            // Bottom Right
            circularAngle = determineCornerCircularAngle(width - x, y - (height - CBR), CBR)
            circularAngle += 90
            x = width - CBR + getDx(CBR, circularAngle)
            y = height - CBR - getDy(CBR, circularAngle)
        } else if(x <= CBL && y >= height - CBL) {
            // Bottom Left
            circularAngle = determineCornerCircularAngle(CBL - x, height - y, CBL)    // Y Parameter is shorter version of "CBL - (y - (height - CBL))"
            circularAngle += 180
            x = CBL + getDx(CBL, circularAngle)
            y = height - CBL - getDy(CBL, circularAngle)
        } else if(x <= CTL && y <= CTL) {
            // Top Left
            circularAngle = determineCornerCircularAngle(x, CTL - y, CTL)
            circularAngle += 270
            x = CTL + getDx(CTL, circularAngle)
            y = CTL - getDy(CTL, circularAngle)
        }
        return Pair(x + getShadowOuterArea(), y + getShadowOuterArea())
    }

    // Determine how much we traverse corner to calculate angle
    // * * * * X * * *
    //               *
    //               *
    //               *
    //               *
    //               *
    //               *
    // For example here we travel 5 unit in X axis, and there is 3 more left,
    // And we didn't travel on y axis. So {max} travel on each axis is 8.
    // And maximum travel angle is 90 degrees. and we traveled 5 + 0 / 8 + 8.
    // So angle is 5 / 16 * 90 = 28.125 degree.
    private fun determineCornerCircularAngle(x: Float, y: Float, max: Float): Float {
        return ((abs(x) + abs(y)) / (2 * abs(max))) * 90f
    }

    // Init background path radii and returns it.
    private fun getBackgroundPathRadii(): FloatArray {
        initBackgroundCornerRadii()
        return background_Path_Radii
    }

    // Init stroke mask radii and returns it.
    private fun getStrokeMaskRadii(): FloatArray {
        initStrokeMaskRadii()
        return stroke_Mask_Radii
    }

    // Init stroke path radii and returns it.
    private fun getStrokePathRadii(): FloatArray {
        initStrokePathRadii()
        return stroke_Path_Radii
    }

    // Init inner shadow external path radii and returns it.
    private fun getInnerShadowExternalPathRadii(inset: Float): FloatArray {
        initInnerShadowExternalPathRadii(inset)
        return innerShadow_External_Path_Radii
    }

    // Adapter for converting capType to Paint.Cap:
    private fun getStrokeCap(capType: CapType): Paint.Cap {
        return when(capType) {
            CapType.Butt -> Paint.Cap.BUTT
            CapType.Square -> Paint.Cap.SQUARE
            CapType.Round -> Paint.Cap.ROUND
        }
    }

    // It's the trigger for corner related mathematics
    // to update themselves according to new values.
    // It makes performance improvement to not calculate,
    // if it is not changed.
    private fun cornerUpdated() {
        background_Path_Radii_Updated = false
        stroke_Path_Radii_Updated = false
        stroke_Mask_Radii_Updated = false
    }

    // Init stroke path and returns it.
    private fun getStrokePath(): Path {
        initStrokePath()
        return stroke_Path
    }

    // Init stroke mask and returns it.
    private fun getStrokeMask(): Path {
        initStrokeMask()
        return stroke_Mask
    }

    // Init stroke paint and returns it.
    private fun getStrokePaint(): Paint {
        initStrokePaint()
        return stroke_Paint
    }

    // Init no stroke path and returns it.
    private fun getNoStrokePath(): Path {
        initNoStrokePath()
        return no_Stroke_Path
    }

    // If the stroke is dashed or not
    private fun isDashed(): Boolean {
        return stroke_Type == StrokeType.Dash
    }

    // Init background path and returns it
    private fun getBackgroundPath(): Path {
        initBackgroundPath()
        return background_Path
    }

    // Init background paint and returns it
    private fun getBackgroundPaint(): Paint {
        initBackgroundPaint()
        return background_Paint
    }

    // It returns 0 if can not draw stroke
    // (because of background color type)
    private fun getStrokeWidth(): Float {
        return if(canDrawStroke())
            stroke_Width
        else
            0f
    }

    // Returns default shadow area size if it is not set,
    // or the actual shadow are it it is set.
    private fun getShadowOuterArea(): Float {
        return if(shadow_Outer_Area == NOT_DEFINED_SHADOW_OUTER_AREA)
            0f
        else
            shadow_Outer_Area
    }

    // Checks blur and sets it.
    private fun setShadowBlur(shadowIndex: Int, blur: Float) {
        shadows[shadowIndex].blur = dimenCheck(blur, null)
    }

    // Checks distance and sets it.
    private fun setShadowDistance(shadowIndex: Int, distance: Float) {
        shadows[shadowIndex].distance = dimenCheck(distance, null)
    }

    // Normalize alpha and sets it.
    private fun setShadowAlpha(shadowIndex: Int, alpha: Float) {
        shadows[shadowIndex].alpha = floatPercentCheck(alpha)
    }

    // It sets the color
    private fun setShadowColor(shadowIndex: Int, color: Int) {
        shadows[shadowIndex].color = color
    }

    // Checks the angle and sets it.
    private fun setShadowAngle(shadowIndex: Int, angle: Float) {
        shadows[shadowIndex].angle = angleCheck(angle)
    }

    // We have array of four shadows,
    // first two are outer shadows,
    // second two are inner shadows,
    // And this function maps raw index (0 or 1) to appropriate shadow index.
    private fun mapShadowIndex(shadowType: ShadowType, shadowIndex: Int): Int? {
        return when (shadowType) {
            ShadowType.Outer -> shadowIndex
            ShadowType.Inner -> shadowIndex + 2
        }
    }

    // Preparing Area
    // *
    // *
    // *

    // *
    // *
    // *
    // External Interface Area

    fun setShadow(shadowType: ShadowType, shadowIndex: Int, blur: Float, distance: Float, angle: Float, alpha: Float, color: Int) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null) {
            setShadowBlur(mappedShadowIndex, blur)
            setShadowDistance(mappedShadowIndex, distance)
            setShadowAlpha(mappedShadowIndex, alpha)
            setShadowColor(mappedShadowIndex, color)
            setShadowAngle(mappedShadowIndex, angle)
        } else
            return
    }

    fun setShadowBlur(shadowType: ShadowType, shadowIndex: Int, blur: Float) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowBlur(mappedShadowIndex, blur)
        else
            return
    }

    fun setShadowDistance(shadowType: ShadowType, shadowIndex: Int, distance: Float) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowDistance(mappedShadowIndex, distance)
        else
            return
    }

    fun setShadowAlpha(shadowType: ShadowType, shadowIndex: Int, alpha: Float) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowAlpha(mappedShadowIndex, alpha)
        else
            return
    }

    fun setShadowColor(shadowType: ShadowType, shadowIndex: Int, color: Int) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowColor(mappedShadowIndex, color)
        else
            return
    }

    fun setShadowAngle(shadowType: ShadowType, shadowIndex: Int, angle: Float) {
        val mappedShadowIndex = mapShadowIndex(shadowType, shadowIndex)
        if(mappedShadowIndex != null)
            setShadowAngle(mappedShadowIndex, angle)
        else
            return
    }

    fun setShadowInnerBlur(shadowIndex: Int, blur: Float) {
        setShadowBlur(shadowIndex + 2, blur)
    }

    fun setShadowInnerDistance(shadowIndex: Int, distance: Float) {
        setShadowDistance(shadowIndex + 2, distance)
    }

    fun setShadowInnerAlpha(shadowIndex: Int, alpha: Float) {
        setShadowAlpha(shadowIndex + 2, alpha)
    }

    fun setShadowInnerColor(shadowIndex: Int, color: Int) {
        setShadowColor(shadowIndex + 2, color)
    }

    fun setShadowInnerAngle(shadowIndex: Int, angle: Float) {
        setShadowAngle(shadowIndex + 2, angle)
    }

    fun setShadowOuterBlur(shadowIndex: Int, blur: Float) {
        setShadowBlur(shadowIndex, blur)
    }

    fun setShadowOuterDistance(shadowIndex: Int, distance: Float) {
        setShadowDistance(shadowIndex, distance)
    }

    fun setShadowOuterAlpha(shadowIndex: Int, alpha: Float) {
        setShadowAlpha(shadowIndex, alpha)
    }

    fun setShadowOuterColor(shadowIndex: Int, color: Int) {
        setShadowColor(shadowIndex, color)
    }

    fun setShadowOuterAngle(shadowIndex: Int, angle: Float) {
        setShadowAngle(shadowIndex, angle)
    }

    fun setCorners(cornerRadius: Float, cornerRadius_TopLeft: Float, cornerRadius_TopRight: Float, cornerRadius_BottomRight: Float, cornerRadius_BottomLeft: Float) {
        this.cornerRadius_ = cornerRadius
        this.cornerRadius_TopLeft = cornerRadius_TopLeft
        this.cornerRadius_TopRight = cornerRadius_TopRight
        this.cornerRadius_BottomRight = cornerRadius_BottomRight
        this.cornerRadius_BottomLeft = cornerRadius_BottomLeft
    }

    fun setCorners(cornerRadius_TopLeft: Float, cornerRadius_TopRight: Float, cornerRadius_BottomRight: Float, cornerRadius_BottomLeft: Float) {
        this.cornerRadius_TopLeft = cornerRadius_TopLeft
        this.cornerRadius_TopRight = cornerRadius_TopRight
        this.cornerRadius_BottomRight = cornerRadius_BottomRight
        this.cornerRadius_BottomLeft = cornerRadius_BottomLeft
    }

    fun haveInnerShadow(): Boolean {
        return shadows[2].blur != NOT_DEFINED_DIMEN || shadows[3].blur != NOT_DEFINED_DIMEN
    }

    fun haveOuterShadow(): Boolean {
        return shadows[0].blur != NOT_DEFINED_DIMEN || shadows[1].blur != NOT_DEFINED_DIMEN
    }

    fun haveAnyShadow(): Boolean {
        return haveInnerShadow() || haveOuterShadow()
    }

    fun getActualWidth(): Float {
        return measuredWidth - getShadowOuterArea() * 2
    }

    fun getActualHeight(): Float {
        return measuredHeight - getShadowOuterArea() * 2
    }

    // External Interface Area
    // *
    // *
    // *

    // *
    // *
    // *
    // Pressing Listener Area

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    // Pressing Listener Area
    // *
    // *
    // *

    // *
    // *
    // *
    // Drawing Area

    // Determine if we can draw background (base on background type)
    private fun canDrawBackground(): Boolean {
        return background_Type == BackgroundType.Fill || background_Type == BackgroundType.Fill_Stroke
    }

    // Determine if we can draw stroke (base on background type)
    private fun canDrawStroke(): Boolean {
        return background_Type == BackgroundType.Stroke || background_Type == BackgroundType.Fill_Stroke
    }

    // Determine if we can draw shadow for specific shadow:
    private fun canDrawShadow(shadow: ShadowObject): Boolean {
        // If shadow type is outer, we check if we have room for drawing shadow:
        if(shadow.shadowType == ShadowType.Outer && getShadowOuterArea() == 0f)
            return false
        // Checks if blur is not zero, and we have at least stroke or background:
        return shadow.blur != NOT_DEFINED_DIMEN && (canDrawStroke() || canDrawBackground())
    }

    private fun drawBackground(canvas: Canvas?) {
        if(canDrawBackground()) {
            canvas?.drawPath(getBackgroundPath(), getBackgroundPaint())
        }
    }

    private fun drawStroke(canvas: Canvas?) {
        if(canDrawStroke()) {
            if(isDashed()) {
                canvas?.save()
                // If stroke is dashed,
                // we use both stroke type drawing and masking
                // for perfect drawing:
                canvas?.clipPath(getStrokeMask())
                canvas?.drawPath(getStrokePath(), getStrokePaint())
                canvas?.restore()
            } else {
                // If stroke is regular, we draw on background rectF,
                // and cut out inner rectF, to create stroke effect:
                canvas?.drawPath(getStrokeMask(), getStrokePaint())
            }
        }
    }

    private fun drawShadow(canvas: Canvas?, shadow: ShadowObject) {
        canvas?.save()
        canvas?.clipPath(shadow.mask)
        canvas?.drawPath(shadow.path, shadow.paint)
        canvas?.restore()
    }

    private fun drawShadows(canvas: Canvas?) {
        for(shadow in shadows)
            if(canDrawShadow(shadow)) {
                // Init shadow styles,
                // and save it in its shadow object:
                initShadow(shadow)
                drawShadow(canvas, shadow)
            }
    }

    private fun drawDebugMode(canvas: Canvas?) {
        if(DEBUG_MODE) {
            initDebugFields()
//            canvas?.drawPath(DEBUG_path, DEBUG_paint_stroke)
            DEBUG_path.reset()
            var startPoint = getLinearGradientCircularStartPoint(stroke_Gradient_Angle)
            var endPoint = getLinearGradientCircularStartPoint(stroke_Gradient_Angle + 180)
            canvas?.drawLine(startPoint.first, startPoint.second, (getActualWidth() / 2) + getShadowOuterArea(), (getActualHeight() / 2) + getShadowOuterArea(), DEBUG_paint_stroke)
//            canvas?.drawPoint(startPoint.first, startPoint.second, DEBUG_paint_fill)
            canvas?.drawCircle(startPoint.first, startPoint.second, 16f, DEBUG_paint_fill)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawStroke(canvas)
        drawShadows(canvas)
        drawDebugMode(canvas)
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        canvas?.clipPath(getNoStrokePath())
        return super.drawChild(canvas, child, drawingTime)
    }

}
