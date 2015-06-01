/*
Copyright (c) 2013-2014 Robby, Kansas State University.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
*/

import sbt._
import Keys._
import scala.collection.mutable._
import java.net.URLClassLoader
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.security._
import java.util.Properties
import java.util.StringTokenizer
import java.io.StringWriter

object SireumBuild extends Build {
  final val BUILD_FILENAME = "BUILD"
  final val PRELUDE_DIR = "prelude/"
  final val CORE_DIR = "core/"
  final val BAKAR_DIR = "bakar/"
  final val PARSER_DIR = "parser/"
  final val CLIENT_SERVER_DIR = "client-server/"
  final val JAWA_DIR = "jawa/"
  final val AMANDROID_DIR = "amandroid/"

  import ProjectInfo._

  lazy val sireum =
    Project(
      id = "sireum",
      settings = sireumSettings,
      base = file(".")) aggregate (
        lib, macr, util, parser,
        pilar, alir, konkrit,
        topi, kiasan,
        extComposite, extCompositeValue,
        option, pipeline, cli, tools, module, server, project,
        coreTest,
        bakarXml, bakarJago, bakarTools, bakarCompiler, bakarTest,
        jawa, jawaAlir, jawaTest,
        amandroid, amandroidAlir, amandroidSecurity, amandroidCli, amandroidTest
      ) settings (
          name := "Sireum")

  final val scalaVer = "2.11.6"

  val sireumSettings = Seq(
    organization := "SAnToS Laboratory",
    artifactName := { (config : ScalaVersion, module : ModuleID, artifact : Artifact) =>
      artifact.name + (
        artifact.classifier match {
          case Some("sources") => "-src"
          case Some("javadoc") => "-doc"
          case _               => ""
        }) + "." + artifact.extension
    },
    incOptions := incOptions.value.withNameHashing(true),
    parallelExecution in Test := false,
    scalaVersion := scalaVer,
    scalacOptions ++= Seq("-target:jvm-1.8", "-Ybackend:GenBCode"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVer,
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVer,
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
  )

  lazy val lib = toSbtProject(libPI)
  lazy val macr = toSbtProject(macroPI)
  lazy val util = toSbtProject(utilPI)
  lazy val parser = toSbtProject(parserPI)
  lazy val pilar = toSbtProject(pilarPI)
  lazy val alir = toSbtProject(alirPI)
  lazy val konkrit = toSbtProject(konkritPI)
  lazy val topi = toSbtProject(topiPI)
  lazy val kiasan = toSbtProject(kiasanPI)
  lazy val extComposite = toSbtProject(extCompositePI)
  lazy val extCompositeValue = toSbtProject(extCompositeValuePI)
  lazy val option = toSbtProject(optionPI)
  lazy val pipeline = toSbtProject(pipelinePI)
  lazy val cli = toSbtProject(cliPI)
  lazy val tools = toSbtProject(toolsPI)
  lazy val module = toSbtProject(modulePI)
  lazy val server = toSbtProject(serverPI)
  lazy val project = toSbtProject(projectPI)
  lazy val coreTest = toSbtProject(coreTestPI)
  lazy val bakarXml = toSbtProject(bakarXmlPI)
  lazy val bakarCompiler = toSbtProject(bakarCompilerPI)
  lazy val bakarJago = toSbtProject(bakarJagoPI)
  lazy val bakarTools = toSbtProject(bakarToolsPI)
  lazy val bakarTest = toSbtProject(bakarTestPI)
  lazy val jawa = toSbtProject(jawaPI)
  lazy val jawaAlir = toSbtProject(jawaAlirPI)
  lazy val jawaTest = toSbtProject(jawaTestPI)
  lazy val amandroid = toSbtProject(amandroidPI)
  lazy val amandroidAlir = toSbtProject(amandroidAlirPI)
  lazy val amandroidSecurity = toSbtProject(amandroidSecurityPI)
  lazy val amandroidCli = toSbtProject(amandroidCliPI)
  lazy val amandroidTest = toSbtProject(amandroidTestPI)

  def firstExists(default : String, paths : String*) : String = {
    for (p <- paths)
      if (new File(p).exists)
        return p
    val f = new File(System.getProperty("user.home") + "/" + default)
    f.mkdirs
    val path = f.getAbsolutePath
    println("Using " + path)
    path
  }

  def toSbtProject(pi : ProjectInfo) : Project =
    Project(
      id = pi.id,
      settings = sireumSettings,
      base = pi.baseDir).
      dependsOn(pi.dependencies.map { p =>
        new ClasspathDependency(new LocalProject(p.id), None)
      } : _*).
      settings(name := pi.name)

  val libPI = new ProjectInfo("Sireum Lib", PRELUDE_DIR, Seq())
  val macroPI = new ProjectInfo("Sireum Macro", PRELUDE_DIR, Seq(), libPI)
  val utilPI = new ProjectInfo("Sireum Util", PRELUDE_DIR, Seq(),
    libPI)
  val parserPI = new ProjectInfo("Sireum Parser", PARSER_DIR, Seq(),
    libPI)
  val pilarPI = new ProjectInfo("Sireum Pilar", CORE_DIR, Seq(),
    libPI, utilPI, parserPI)
  val alirPI = new ProjectInfo("Sireum Alir", CORE_DIR, Seq(),
    libPI, utilPI, pilarPI)
  val konkritPI = new ProjectInfo("Sireum Konkrit", CORE_DIR, Seq(),
    libPI, utilPI, pilarPI)
  val topiPI = new ProjectInfo("Sireum Topi", CORE_DIR, Seq(),
    libPI, utilPI, pilarPI)
  val kiasanPI = new ProjectInfo("Sireum Kiasan", CORE_DIR, Seq(),
    utilPI, pilarPI, konkritPI, topiPI)
  val extCompositePI = new ProjectInfo("Sireum Extension Composite",
    CORE_DIR, Seq(),
    libPI, utilPI, pilarPI)
  val extCompositeValuePI = new ProjectInfo("Sireum Extension Composite Value",
    CORE_DIR, Seq(),
    libPI, utilPI, pilarPI, konkritPI, kiasanPI, extCompositePI)
  val optionPI = new ProjectInfo("Sireum Option", CORE_DIR, Seq(),
    macroPI, utilPI)
  val pipelinePI = new ProjectInfo("Sireum Pipeline", CORE_DIR, Seq(),
    libPI, utilPI)
  val cliPI = new ProjectInfo("Sireum CLI", CORE_DIR, Seq(),
    utilPI, optionPI, pipelinePI)
  val toolsPI = new ProjectInfo("Sireum Tools", CORE_DIR, Seq(),
    utilPI, optionPI, pipelinePI, cliPI)
  val modulePI = new ProjectInfo("Sireum Module", CORE_DIR, Seq(),
    libPI, utilPI, pilarPI, alirPI, optionPI, pipelinePI, toolsPI)
  val serverPI = new ProjectInfo("Sireum Server", CLIENT_SERVER_DIR, Seq(),
    libPI, utilPI, optionPI)
  val projectPI = new ProjectInfo("Sireum Project", CLIENT_SERVER_DIR, Seq(),
    libPI, utilPI, optionPI, serverPI)
  val coreTestPI = new ProjectInfo("Sireum Core Test", CORE_DIR, Seq(),
    libPI, utilPI, pilarPI, alirPI, konkritPI, topiPI, kiasanPI,
    optionPI, pipelinePI, cliPI, modulePI, extCompositePI, extCompositeValuePI)
  val bakarXmlPI = new ProjectInfo("Sireum Bakar XML",
    BAKAR_DIR, Seq("Gnat"),
    libPI, utilPI, pipelinePI, optionPI)
  val bakarJagoPI = new ProjectInfo("Sireum Bakar Jago", BAKAR_DIR, Seq("Gnat"),
    libPI, utilPI, pipelinePI, bakarXmlPI)
  val bakarToolsPI = new ProjectInfo("Sireum Bakar Tools", BAKAR_DIR, Seq("Gnat"),
    libPI, utilPI, bakarXmlPI, bakarJagoPI)
  val bakarCompilerPI = new ProjectInfo("Sireum Bakar Compiler",
    BAKAR_DIR, Seq("Gnat"),
    libPI, utilPI, pilarPI, pipelinePI, modulePI, bakarXmlPI,
    konkritPI, kiasanPI)
  val bakarTestPI = new ProjectInfo("Sireum Bakar Test", BAKAR_DIR, Seq("Gnat"),
    bakarCompilerPI, bakarJagoPI, bakarXmlPI, coreTestPI, libPI, optionPI,
    pilarPI, pipelinePI, utilPI)
  val jawaPI = new ProjectInfo("Sireum Jawa",
    JAWA_DIR, Seq(),
    libPI, utilPI, pilarPI, parserPI)
  val jawaAlirPI = new ProjectInfo("Sireum Jawa Alir",
    JAWA_DIR, Seq(),
    libPI, utilPI, pilarPI, parserPI, alirPI, jawaPI)
  val jawaTestPI = new ProjectInfo("Sireum Jawa Test",
    JAWA_DIR, Seq(),
    libPI, utilPI, pilarPI, parserPI, alirPI, jawaPI, jawaAlirPI)
  val amandroidPI = new ProjectInfo("Sireum Amandroid",
    AMANDROID_DIR, Seq("Amandroid"),
    libPI, utilPI, pilarPI, parserPI, alirPI, optionPI, jawaPI, jawaAlirPI)
  val amandroidAlirPI = new ProjectInfo("Sireum Amandroid Alir",
    AMANDROID_DIR, Seq("Amandroid"),
    libPI, utilPI, pilarPI, parserPI, alirPI, optionPI, jawaPI, jawaAlirPI, amandroidPI)
  val amandroidSecurityPI = new ProjectInfo("Sireum Amandroid Security",
    AMANDROID_DIR, Seq("Amandroid"),
    libPI, utilPI, pilarPI, parserPI, alirPI, optionPI, jawaPI, jawaAlirPI, amandroidPI, amandroidAlirPI)
  val amandroidCliPI = new ProjectInfo("Sireum Amandroid Cli",
    AMANDROID_DIR, Seq("Amandroid"),
    libPI, utilPI, pilarPI, parserPI, alirPI, optionPI, jawaPI, jawaAlirPI, amandroidPI, amandroidAlirPI, amandroidSecurityPI)
  val amandroidTestPI = new ProjectInfo("Sireum Amandroid Test",
    AMANDROID_DIR, Seq("Amandroid"),
    libPI, utilPI, pilarPI, parserPI, alirPI, optionPI, jawaPI, jawaAlirPI, amandroidPI,
    amandroidAlirPI, amandroidSecurityPI, jawaTestPI)
}
