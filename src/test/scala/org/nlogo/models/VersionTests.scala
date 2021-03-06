package org.nlogo.models

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.util.Failure
import scala.util.Try

import org.apache.commons.io.FileUtils.listFiles

class VersionTests extends TestModels {

  val allModelTries = listFiles(Model.modelDir, Model.extensions, true).asScala
    .map(f => f -> Try(Model.apply(f)))
  def allModelFailures = allModelTries.collect { case (f, Failure(e)) => (f, e) }

  test("All models are readable") {
    if (allModelFailures.nonEmpty) fail(
      "The following models failed:" +
        allModelFailures.map { case (f, e) => "  \"" + f.getCanonicalPath + "\"" }.mkString("\n") +
        "Details:\n" +
        allModelFailures.map {
          case (f, e) =>
            "\"" + f.getCanonicalPath + "\"\n" +
              e + "\n" +
              e.getStackTrace.mkString("\n")
        }.mkString("\n\n")
    )
  }

  val acceptedVersions = Set("NetLogo 5.2.0", "NetLogo 3D 5.2.0")
  testAllModels("Version should be one of " + acceptedVersions.mkString(", ")) {
    for {
      model <- _
      if !acceptedVersions.contains(model.version.trim)
    } yield model.quotedPath + "\n  " + model.version
  }
}
