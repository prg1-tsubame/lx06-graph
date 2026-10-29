import sbt._
import Keys._

object Programming1 {
  val COMMON = Seq(
      ThisBuild / version      := "0.1.0",
      ThisBuild / organization := "jp.ac.isct.is.prg1",

      Test / fork         := true,
      Test / connectInput := true,
      Test / logBuffered  := true,

      run / fork          := true,
      run / connectInput  := true,
      Global / cancelable := true,
    )

  val SCALA3 = Seq(
      ThisBuild / scalaVersion := "3.9.0",                   // scalac コンパイラのバージョン
      Compile / scalaSource := baseDirectory.value / "src",  // Scala のソース置き場のディレクトリ
      //resourceDirectory := baseDirectory.value / "resources",
      scalacOptions ++= Seq(
        "-explain",
        //"-deprecate",
        "-Werror",                                         // 警告をエラーとして扱う
        // "-Xlint",
        // -Wunused, -Yrangepos は Scala3 では使わないこと。詳しくは [The essential Scala build tool tutorial](https://www.scalawilliam.com/essential-sbt/) を参照のこと
      ),
    )

  val SCALA_TEST = Seq(
      Test / scalaSource := baseDirectory.value / "test",

      // 以下は [ScalaTest についての設定](https://www.scalatest.org/install)
      libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.20",

      // test configuration のみに scalatest を読み込む（ライブラリ依存性 / マネージ依存性）
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % "test",
    )

  val SCALA_FX = Seq(
      // https://mvnrepository.com/artifact/org.scalafx/scalafx
      libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33",
      // https://mvnrepository.com/artifact/org.scalafx/scalafx-extras
      libraryDependencies += "org.scalafx" %% "scalafx-extras" % "0.9.0",
    )

  val Scala3 = COMMON ++ SCALA3 ++ SCALA_TEST
  val Scala3FX = COMMON ++ SCALA3 ++ SCALA_FX
}
