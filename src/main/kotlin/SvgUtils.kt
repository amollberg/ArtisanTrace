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
    }
}
