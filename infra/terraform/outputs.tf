output "ec2_public_ip" {
  value = aws_instance.api_server.public_ip
}

output "api_url" {
  value = "http://${aws_instance.api_server.public_ip}:8080"
}

output "rds_endpoint" {
  value = aws_db_instance.app_db.address
}