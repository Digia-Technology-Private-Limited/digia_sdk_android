//package com.digia.digiaui.framework.actions.showBottomSheet
//
//import android.content.Context
//import com.digia.digiaui.framework.RenderPayload
//import com.digia.digiaui.framework.UIResources
//import com.digia.digiaui.framework.actions.base.Action
//import com.digia.digiaui.framework.actions.base.ActionFlow
//import com.digia.digiaui.framework.actions.base.ActionId
//import com.digia.digiaui.framework.actions.base.ActionProcessor
//import com.digia.digiaui.framework.actions.base.ActionType
//import com.digia.digiaui.framework.expr.ScopeContext
//import com.digia.digiaui.framework.models.ExprOr
//import com.digia.digiaui.framework.state.StateContext
//import com.digia.digiaui.framework.utils.JsonLike
//
//
//
///**
// * ShowBottomSheet Action
// *
// * Displays a bottom sheet modal with specified content.
// */
//data class ShowBottomSheetAction(
//    override var actionId: ActionId? = null,
//    override var disableActionIf: ExprOr<Boolean>? = null,
//    val viewData: ExprOr<JsonLike>?,
//    val waitForResult: Boolean = false,
//    val style: JsonLike?,
//    val onResult: ActionFlow?
//) : Action {
//    override val actionType = ActionType.SHOW_BOTTOM_SHEET
//
//    override fun toJson(): JsonLike =
//        mapOf(
//            "type" to actionType.value,
//            "viewData" to viewData?.toJson(),
//            "waitForResult" to waitForResult,
//            "style" to style,
//            "onResult" to onResult?.toJson()
//        )
//
//    companion object {
//        fun fromJson(json: JsonLike): ShowBottomSheetAction {
//            return ShowBottomSheetAction(
//                viewData = ExprOr.fromJson<JsonLike>(json["viewData"]),
//                waitForResult = json["waitForResult"] as? Boolean ?: false,
//                style = json["style"] as? JsonLike,
//                onResult = ActionFlow.fromJson(json["onResult"])
//            )
//        }
//    }
//}
//
//
///** Processor for show bottom sheet action */
//class ShowBottomSheetProcessor : ActionProcessor<ShowBottomSheetAction>() {
//    override fun execute(
//        context: Context,
//        action: ShowBottomSheetAction,
//        scopeContext: ScopeContext?,
//        stateContext: StateContext?,
//        resourceProvider: UIResources?,
//        id: String
//    ): Any? {
//        // Evaluate pageId
//        val pageId = action.pageId?.evaluate(scopeContext) ?: ""
//        if (pageId.isEmpty()) {
//            println("ShowBottomSheetAction: pageId is empty")
//            return null
//        }
//
//        // Evaluate options
//        val isDismissible = action.isDismissible?.evaluate(scopeContext) ?: true
//        val enableDrag = action.enableDrag?.evaluate(scopeContext) ?: true
//        val isScrollControlled = action.isScrollControlled?.evaluate(scopeContext) ?: false
//        val backgroundColor = action.backgroundColor?.evaluate<String>(scopeContext)
//
//        println("ShowBottomSheetAction: Showing bottom sheet with pageId: $pageId")
//        println("  isDismissible: $isDismissible, enableDrag: $enableDrag")
//        println("  isScrollControlled: $isScrollControlled, backgroundColor: $backgroundColor")
//
//        // TODO: Implement bottom sheet display
//        // This requires integration with Compose ModalBottomSheet
//        // The implementation would need to:
//        // 1. Get the BottomSheetState from CompositionLocal
//        // 2. Load the page content using pageId
//        // 3. Show the bottom sheet with the configured options
//        // 4. Handle dismissal and callbacks
//
//        return null
//    }
//}
