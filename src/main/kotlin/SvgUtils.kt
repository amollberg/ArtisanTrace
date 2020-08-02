class SvgUtils {
    companion object {
        // Set a black background when opened in Inkscape
        fun addBlackBackground(svgText: String): String =
            svgText.replace(
                Regex("""(\<svg[^\>]*\>)"""),
                replacement = """$1<sodipodi:namedview
                                    pagecolor="#000000"
                                    bordercolor="#666666"
                                    borderopacity="1"
                                   />"""
            )

        // Remove the dimensions that are hardcoded by OpenRNDR and probably
        // unsuitable for the content
        fun removeHardcodedDimensions(svgText: String): String =
            svgText.replace(
                Regex("""xmlns:xlink="http://www.w3.org/1999/xlink"  x="0px" y="0px"\n width="2676px" height="2048px">"""),
                replacement = """xmlns:xlink="http://www.w3.org/1999/xlink"  x="0px" y="0px">"""
            )
    }
}
