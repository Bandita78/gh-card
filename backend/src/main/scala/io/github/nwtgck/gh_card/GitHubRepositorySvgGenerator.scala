package io.github.nwtgck.gh_card

import scala.xml.Elem

object GitHubRepositorySvgGenerator {
  // TODO: Make it declarative
  // TODO: Support non-English description
  private def descriptionToLines(description: String): List[String] = {
    val splitter = " "
    val words = description.split(splitter)
    var line: String = ""
    var lines: List[String] = List.empty
    for(word <- words) {
      line += word + splitter
      if (line.length > 55) {
        lines :+= line
        line = ""
      }
    }
    if (line != "") {
      lines :+= line
    }
    lines
  }

  // GitHub number string for stars and forks
  private def gitHubNumberString(number: Int): String = {
    if (number < 950) {
      number.toString
    } else {
      val k = number.toFloat / 1000
      if (k < 99.9) {
        "%.1fk" format k
      } else {
        s"${Math.round(k)}k"
      }
    }
  }

  private def getGitHubRepositoryUrl(ownerName: String, shortRepoName: String): String = {
    s"https://github.com/${ownerName}/${shortRepoName}"
  }

  private def getGitHubStargazersUrl(ownerName: String, shortRepoName: String): String = {
    s"https://github.com/${ownerName}/${shortRepoName}/stargazers"
  }

  private def getGitHubNetworkMemberUrl(ownerName: String, shortRepoName: String): String = {
    s"https://github.com/${ownerName}/${shortRepoName}/network/members"
  }

  def generateSvg(ownerName: String, shortRepoName: String, useFullName: Boolean, linkTarget: String, languageOpt: Option[String], description: String, nStars: Int, nForks: Int): Elem = {
    // Name on image
    val repoNameInImage: String = if (useFullName) s"${ownerName}/${shortRepoName}" else shortRepoName

    // Get language color
    val languageColorOpt: Option[String] = languageOpt.map(GitHubLanguageColors.colors)
    // Convert description to lines
    val descriptionLines: List[String] = descriptionToLines(
      // Render emoji
      emoji.EmojiRenderer.render(description)
    )

    // Create description element and last y coordinate
    val (descriptionElems, lastDescriptionY): (List[scala.xml.Elem], Int) = {
      var y: Int = 65
      val diff = 21
      val elem = for(line <- descriptionLines) yield {
        val e =
          <g fill="#586069" fill-opacity="1" stroke="#586069" stroke-opacity="1" stroke-width="1" stroke-linecap="square" stroke-linejoin="bevel" transform="matrix(1,0,0,1,0,0)">
            <text fill="#586069" fill-opacity="1" stroke="none" xml:space="preserve" x="17" y={s"${y}"} font-family="sans-serif" font-size="14" font-weight="400" font-style="normal">{line}</text>
          </g>
        y += diff
        e
      }
      (elem, y - diff)
    }

    // Define board's height
    val height: Int = lastDescriptionY + 43
    // X coordinate next to language
    val languageNextX: Int = languageOpt match {
      case Some(language) =>
        60 + (5 * language.length)
      case _ =>
        16
    }

    // Star icon
    val starIcon: Elem = (<a target={linkTarget} href={s"${getGitHubStargazersUrl(ownerName, shortRepoName)}"}><path vector-effect="none" fill-rule="evenodd" d="M14,6 L9.1,5.36 L7,1 L4.9,5.36 L0,6 L3.6,9.26 L2.67,14 L7,11.67 L11.33,14 L10.4,9.26 L14,6" /></a>)
    // Fork icon
    val forkIcon: Elem = (<a target={linkTarget} href={s"${getGitHubNetworkMemberUrl(ownerName, shortRepoName)}"}><path vector-effect="none" fill-rule="evenodd" d="M10,5 C10,3.89 9.11,3 8,3 C7.0966,2.99761 6.30459,3.60318 6.07006,4.47561 C5.83554,5.34804 6.21717,6.2691 7,6.72 L7,7.02 C6.98,7.54 6.77,8 6.37,8.4 C5.97,8.8 5.51,9.01 4.99,9.03 C4.16,9.05 3.51,9.19 2.99,9.48 L2.99,4.72 C3.77283,4.2691 4.15446,3.34804 3.91994,2.47561 C3.68541,1.60318 2.8934,0.997613 1.99,1 C0.88,1 0,1.89 0,3 C0.00428689,3.71022 0.384911,4.3649 1,4.72 L1,11.28 C0.41,11.63 0,12.27 0,13 C0,14.11 0.89,15 2,15 C3.11,15 4,14.11 4,13 C4,12.47 3.8,12 3.47,11.64 C3.56,11.58 3.95,11.23 4.06,11.17 C4.31,11.06 4.62,11 5,11 C6.05,10.95 6.95,10.55 7.75,9.75 C8.55,8.95 8.95,7.77 9,6.73 L8.98,6.73 C9.59,6.37 10,5.73 10,5 M2,1.8 C2.66,1.8 3.2,2.35 3.2,3 C3.2,3.65 2.65,4.2 2,4.2 C1.35,4.2 0.8,3.65 0.8,3 C0.8,2.35 1.35,1.8 2,1.8 M2,14.21 C1.34,14.21 0.8,13.66 0.8,13.01 C0.8,12.36 1.35,11.81 2,11.81 C2.65,11.81 3.2,12.36 3.2,13.01 C3.2,13.66 2.65,14.21 2,14.21 M8,6.21 C7.34,6.21 6.8,5.66 6.8,5.01 C6.8,4.36 7.35,3.81 8,3.81 C8.65,3.81 9.2,4.36 9.2,5.01 C9.2,5.66 8.65,6.21 8,6.21 " /></a>)

    // TODO: Support forked-repo icon
    // TODO: Simplify conditional-branch for star and forks
    // (NOTE: generated by https://github.com/github/personal-website and http://www.hiqpdf.com/demo/ConvertHtmlToSvg.aspx)
    // <?xml version="1.0" encoding="UTF-8"?>
    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="442" height={s"${height + 1}"} version="1.2" baseProfile="tiny">
      <defs />
      <g fill="none" stroke="black" stroke-width="1" fill-rule="evenodd" stroke-linecap="square" stroke-linejoin="bevel">
        <g fill="#ffffff" fill-opacity="1" stroke="none" transform="matrix(1,0,0,1,0,0)">
          <rect x="0" y="0" width="440" height={s"${height + 1}"} />
        </g>
        <!-- Boarder -->
        <rect x="0" y="0" width="441" height={s"${height}"} stroke="#eaecef" stroke-width="2" />
        <g fill="#586069" fill-opacity="1" stroke="none" transform="matrix(1.25,0,0,1.25,17,21)">
          <!-- Repo icon like book -->
          <path vector-effect="none" fill-rule="evenodd" d="M4,9 L3,9 L3,8 L4,8 L4,9 M4,6 L3,6 L3,7 L4,7 L4,6 M4,4 L3,4 L3,5 L4,5 L4,4 M4,2 L3,2 L3,3 L4,3 L4,2 M12,1 L12,13 C12,13.55 11.55,14 11,14 L6,14 L6,16 L4.5,14.5 L3,16 L3,14 L1,14 C0.45,14 0,13.55 0,13 L0,1 C0,0.45 0.45,0 1,0 L11,0 C11.55,0 12,0.45 12,1 M11,11 L1,11 L1,13 L3,13 L3,12 L6,12 L6,13 L11,13 L11,11 M11,1 L2,1 L2,10 L11,10 L11,1" />
        </g>
        <g fill="#0366d6" fill-opacity="1" stroke="#0366d6" stroke-opacity="1" stroke-width="1" stroke-linecap="square" stroke-linejoin="bevel" transform="matrix(1,0,0,1,0,0)">
          <!-- Repo name -->
          <a target={linkTarget} href={s"${getGitHubRepositoryUrl(ownerName, shortRepoName)}"}>
            <text fill="#0366d6" fill-opacity="1" stroke="none" xml:space="preserve" x="41" y="33" font-family="sans-serif" font-size="16" font-weight="630" font-style="normal">{repoNameInImage}</text>
          </a>
        </g>
        <!-- Description -->
        {descriptionElems}
        {
        languageOpt match {
          case Some(language) =>
            <!-- Language  -->
            <g fill="#24292e" fill-opacity="1" stroke="#24292e" stroke-opacity="1" stroke-width="1" stroke-linecap="square" stroke-linejoin="bevel" transform="matrix(1,0,0,1,0,0)">
              <text fill="#24292e" fill-opacity="1" stroke="none" xml:space="preserve" x="33" y={s"${lastDescriptionY + 26}"} font-family="sans-serif" font-size="12" font-weight="400" font-style="normal">{language}</text>
            </g>
          case _ => ()
        }
        }
        <!-- Star or Fork icon or none -->
        <g fill="#000000" fill-opacity="1" stroke="none" transform={s"matrix(1,0,0,1,${languageNextX},${lastDescriptionY + 13})"}>{
          if (nStars > 0) {
            starIcon
          } else if (nForks > 0){
            forkIcon
          }
        }</g>
        <g fill="#586069" fill-opacity="1" stroke="#586069" stroke-opacity="1" stroke-width="1" stroke-linecap="square" stroke-linejoin="bevel" transform="matrix(1,0,0,1,0,0)">
          <!-- The number of stars or fork -->
          {
            // Url & Number of stars or forks
            val urlAndNumberStr: Option[(String, String)] =
              if (nStars > 0) {
                Some((getGitHubStargazersUrl(ownerName, shortRepoName), gitHubNumberString(nStars)))
              } else if (nForks > 0){
                Some((getGitHubNetworkMemberUrl(ownerName, shortRepoName), gitHubNumberString(nForks)))
              } else {
                None
              }

            urlAndNumberStr match {
              case Some((url, numberStr)) =>
                <a target={linkTarget} href={url}>
                  <text fill="#586069" fill-opacity="1" stroke="none" xml:space="preserve" x={s"${languageNextX + 21}"} y={s"${lastDescriptionY + 26}"} font-family="sans-serif" font-size="12" font-weight="400" font-style="normal">{
                    numberStr
                  }</text>
                </a>
              case _ =>
                ()
            }
          }
        </g>

        <!-- Fork icon or none -->
        <g fill="#000000" fill-opacity="1" stroke="none" transform={s"matrix(1,0,0,1,${languageNextX + 63},${lastDescriptionY + 13})"}>{
          if (nStars > 0 && nForks > 0){
            forkIcon
          }
        }</g>
        <g fill="#586069" fill-opacity="1" stroke="#586069" stroke-opacity="1" stroke-width="1" stroke-linecap="square" stroke-linejoin="bevel" transform="matrix(1,0,0,1,0,0)">
          <!-- The number of forks or none -->
          {
            if (nStars > 0 && nForks > 0) {
              <a target={linkTarget} href={s"${getGitHubNetworkMemberUrl(ownerName, shortRepoName)}"}>
                <text fill="" fill-opacity="1" stroke="none" xml:space="preserve" x={s"${languageNextX + 80}"} y={s"${lastDescriptionY + 26}"} font-family="sans-serif" font-size="12" font-weight="400" font-style="normal">{gitHubNumberString(nForks)}</text>
              </a>
            }
          }
        </g>
        {
        languageColorOpt match {
          case Some(languageColor) =>
            <!-- Language color -->
            <circle cx="23" cy={s"${lastDescriptionY + 21}"} r="7" stroke="none" fill={s"${languageColor}"}/>
          case _ => ()
        }
        }
      </g>
    </svg>
  }
}
