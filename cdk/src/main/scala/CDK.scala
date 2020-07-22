import software.amazon.awscdk.core.{App, Aws, Environment, Stack, StackProps}
import software.amazon.awscdk.services.ec2.Vpc
import software.amazon.awscdk.services.ecs.patterns.{ApplicationLoadBalancedFargateService, ApplicationLoadBalancedTaskImageOptions}
import software.amazon.awscdk.services.ecs.{Cluster, ContainerImage, FargatePlatformVersion}

object CDK {
  val stackName = "fargate-issue"

  def main(args: Array[String]): Unit = {
    val app: App = new App()

    val env        = Environment.builder.region(Aws.REGION).account(Aws.ACCOUNT_ID).build
    val stackProps = StackProps.builder.env(env).build

    IssueStack(app, stackProps).create()
    app.synth
  }
}

case class IssueStack(app: App, stackProps: StackProps) extends Stack(app, "fargate-issue", stackProps) {
  def create(): Unit = {
    val vpc = Vpc.Builder.create(this, "MyVpc")
      .maxAzs(3)  // Default is all AZs in region
      .build();

    val cluster = Cluster.Builder.create(this, "MyCluster")
      .vpc(vpc).build();

    // Create a load-balanced Fargate service and make it public
    ApplicationLoadBalancedFargateService.Builder.create(this, "MyFargateService")
      .cluster(cluster)           // Required
      .cpu(512)                   // Default is 256
      .desiredCount(1) // Default is 1
      .taskImageOptions(
        ApplicationLoadBalancedTaskImageOptions.builder()
          .image(ContainerImage.fromAsset("./target/docker"))
          .build())
      .memoryLimitMiB(2048)       // Default is 512
      .publicLoadBalancer(true)   // Default is false
      .platformVersion(FargatePlatformVersion.VERSION1_4)
      .build();
  }
}