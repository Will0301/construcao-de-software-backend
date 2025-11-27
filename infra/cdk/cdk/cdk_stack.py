from constructs import Construct
import aws_cdk as cdk
from aws_cdk import (
    Stack,
    aws_ec2 as ec2,
    aws_iam as iam,
    aws_s3_assets as s3_assets,
)


class CdkStack(Stack):

    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        # VPC padrão (default) da conta AWS
        vpc = ec2.Vpc.from_lookup(
            self,
            "DefaultVpc",
            is_default=True,
        )

        # Security Group liberando porta 8080
        sg = ec2.SecurityGroup(
            self,
            "SpringSg",
            vpc=vpc,
            allow_all_outbound=True,
        )
        sg.add_ingress_rule(
            ec2.Peer.any_ipv4(),
            ec2.Port.tcp(8080),
            "Allow HTTP 8080",
        )

        # JAR do Spring Boot (gerado no teu backend)
        jar_asset = s3_assets.Asset(
            self,
            "SpringJar",
            path="../target/app.jar",  # ajuste este caminho se teu JAR tiver outro nome
        )

        # Role da EC2 (permite usar SSM / Session Manager)
        role = iam.Role(
            self,
            "Ec2Role",
            assumed_by=iam.ServicePrincipal("ec2.amazonaws.com"),
        )
        role.add_managed_policy(
            iam.ManagedPolicy.from_aws_managed_policy_name(
                "AmazonSSMManagedInstanceCore"
            )
        )

        # Script de inicialização da máquina
        user_data = ec2.UserData.for_linux()
        user_data.add_commands(
            "yum update -y",
            "yum install -y java-17-amazon-corretto",
            f"aws s3 cp {jar_asset.s3_object_url} /home/ec2-user/app.jar",
            "nohup java -jar /home/ec2-user/app.jar > /home/ec2-user/app.log 2>&1 &",
        )

        # Criar a máquina EC2
        instance = ec2.Instance(
            self,
            "SpringInstance",
            vpc=vpc,
            instance_type=ec2.InstanceType("t3.micro"),
            machine_image=ec2.MachineImage.latest_amazon_linux2(),
            security_group=sg,
            role=role,
            user_data=user_data,
        )

        # Permitir leitura do JAR no S3
        jar_asset.grant_read(instance.role)
