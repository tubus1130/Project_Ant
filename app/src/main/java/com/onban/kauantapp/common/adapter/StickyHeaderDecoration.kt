package com.onban.kauantapp.common.adapter

import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/*
https://stackoverflow.com/questions/32949971/how-can-i-make-sticky-headers-in-recyclerview-without-external-lib
 */

class StickyHeaderItemDecoration(private val sectionCallback: SectionCallback) : RecyclerView.ItemDecoration() {

    /*
    onDraw의 경우 ItemView가 그려지기 이전에 그려진다. 따라서 ItemView의 아래에 보이게 된다.

    onDrawOver의 경우는 반대로 ItemView보다 나중에 그려지며 ItemView의 위에 덮어 씌워진다.
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        Log.d("Test", "onDrawOver")
        /*
        유저에게 보이는 가장 최상단 view
         */
        val topChild = parent.getChildAt(0) ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }
        Log.d("Test", "topChildPosition $topChildPosition")

        /* 헤더 */
        val currentHeader: View = sectionCallback.getHeaderLayoutView(parent, topChildPosition) ?: return

        //얘를 그려주려면 measure()로 크기를 측정해야하고  onLayout()으로  위치를 잡아줘야함
        fixLayoutSize(parent, currentHeader, topChild.measuredHeight)

        val contactPoint = currentHeader.bottom
        /* 바로 밑에 있는 놈 */
        val childInContact: View = getChildInContact(parent, contactPoint) ?: return //얘는 parent.getChildAt(1) 로 구할 수 있지 않나?

        val childAdapterPosition = parent.getChildAdapterPosition(childInContact) //얘는 항상 topChildPosition + 1 아닌가?
        Log.d("Test", "childAdapterPosition $childAdapterPosition")
        if (childAdapterPosition == -1)
            return

        when {
            //childAdapterPosition 번째 데이터가 헤더 데이터니?
            sectionCallback.isHeader(childAdapterPosition) ->
                moveHeader(c, currentHeader, childInContact)
            else ->
                drawHeader(c, currentHeader)
        }
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.bottom > contactPoint) {
                if (child.top <= contactPoint) {
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    /*
    여기서 의문인 게
    canvas는 누가 가지고 있는 건가?
    ItemDecoration 코드 주석을 읽어보면
    Draw any appropriate decorations into the Canvas supplied to the RecyclerView.
    리사이클러뷰에서 제공된다고 한다. 리사이클러뷰의 view 들의 canvas와 관계 없는 건가?
    translate 이 canvas의 좌표계를 옮기는 것인데,  header만 움직이고 recyclerview의 view들은 따라 안 움직이는지 의문이다.
    아마 ItemDecoration 에게 제공되는 canvas와 recyclerview의 canvas는 다른 것인가?
    canvas는 누구에 의해서 제공되어 지나?
     */
    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0f, nextHeader.top - currentHeader.height.toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    /**
     * Measures the header view to make sure its size is greater than 0 and will be drawn
     * https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations
     */
    private fun fixLayoutSize(parent: ViewGroup, view: View, height: Int) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            parent.width,
            View.MeasureSpec.EXACTLY
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(
            parent.height,
            View.MeasureSpec.EXACTLY
        )
        val childWidth: Int = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeight: Int = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            height
        )
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface SectionCallback {
        fun isHeader(position: Int): Boolean
        fun getHeaderLayoutView(list: RecyclerView, position: Int): View?
    }
}