#######################################
# SECURITY GROUP
#######################################

resource "aws_security_group" "api_sg" {
  name_prefix = "api-sg-"

  # Permite acesso HTTP público
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Permite acesso SSH (caso precise)
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Permite acesso da EC2 ao banco RDS
  egress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Permite saída geral de rede
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "api-security-group"
  }
}

#######################################
# RDS (POSTGRESQL)
#######################################

resource "aws_db_instance" "app_db" {
  allocated_storage    = 20
  engine               = "postgres"
  engine_version       = "15"
  instance_class       = "db.t3.micro"
  db_name              = "meubanco"
  username             = "admin"
  password             = "MinhaSenhaSecreta123"
  parameter_group_name = "default.postgres15"
  skip_final_snapshot  = true
  publicly_accessible  = true
  vpc_security_group_ids = [aws_security_group.api_sg.id]

  tags = {
    Name = "app-db"
  }
}

#######################################
# IAM ROLE + POLICY PARA SSM
#######################################

resource "aws_iam_role" "ec2_ssm_role" {
  name = "ec2-ssm-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_core" {
  role       = aws_iam_role.ec2_ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ec2_ssm_profile" {
  name = "ec2-ssm-profile"
  role = aws_iam_role.ec2_ssm_role.name
}

#######################################
# INSTÂNCIA EC2
#######################################

resource "aws_instance" "api_server" {
  ami                         = "ami-0c02fb55956c7d316" # Amazon Linux 2023 (us-east-1)
  instance_type               = var.instance_type
  key_name                    = var.key_name
  vpc_security_group_ids      = [aws_security_group.api_sg.id]
  associate_public_ip_address = true
  iam_instance_profile        = aws_iam_instance_profile.ec2_ssm_profile.name

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              amazon-linux-extras install docker -y
              yum install -y amazon-ssm-agent
              systemctl enable docker
              systemctl start docker
              systemctl enable amazon-ssm-agent
              systemctl start amazon-ssm-agent

              docker pull willweyh/arq-software:latest
              docker run -d -p 8080:8080 \
                -e SPRING_DATASOURCE_URL=jdbc:postgresql://${aws_db_instance.app_db.address}:5432/meubanco \
                -e SPRING_DATASOURCE_USERNAME=${aws_db_instance.app_db.username} \
                -e SPRING_DATASOURCE_PASSWORD=${aws_db_instance.app_db.password} \
                willweyh/arq-software:latest
              EOF

  tags = {
    Name = "api-server"
  }
}
