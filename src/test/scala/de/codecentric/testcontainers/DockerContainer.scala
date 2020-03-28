package de.codecentric.testcontainers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.s3.scaladsl.S3
import akka.stream.alpakka.s3.{ApiVersion, MemoryBufferType, MultipartUploadResult, S3Attributes, S3Settings}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, Attributes}
import akka.util.ByteString
import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.testcontainers.containers.wait.strategy.Wait
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.regions.providers.AwsRegionProvider

import scala.concurrent.Future

trait BaseTest extends AnyWordSpec with Matchers with ScalaFutures

class DockerContainer extends BaseTest with ForAllTestContainer {
  override val container: GenericContainer = GenericContainer(
    "localstack/localstack:0.10.7",
    waitStrategy = Wait.forLogMessage("^Ready[.].*", 1),
    env = Map("SERVICES" -> "s3"),
    exposedPorts = Seq(4572))

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(
    Span(10, Seconds))

  "Some test" should {
    "work" in {
      implicit val actorSystem: ActorSystem = ActorSystem()
      implicit val materializer: ActorMaterializer = ActorMaterializer()

      val s3Port = container.mappedPort(4572)

      implicit val localS3Attrs: Attributes =
        S3Attributes.settings(
          S3Settings(
            MemoryBufferType,
            () =>
              new AwsCredentials {
                override def accessKeyId(): String = "localstack"

                override def secretAccessKey(): String = "localstack"
            },
            new AwsRegionProvider {
              override def getRegion: Region = Region.US_EAST_1
            },
            ApiVersion.ListBucketVersion2
          ).withEndpointUrl(s"http://127.0.0.1:$s3Port")
            .withPathStyleAccess(true))

      val bucket = "test-bucket"

      S3.makeBucket(bucket).futureValue
    }
  }
}
