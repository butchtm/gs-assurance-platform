output "private_subnet_id" {
  value = aws_subnet.gs_subnet_private.id
}
output "public_subnet_id" {
  value = aws_subnet.gs_subnet_public.id
}
output "public_subnet_id_2" {
  value = aws_subnet.gs_subnet_public_2.id
}
output "database_subnet_id" {
  value = aws_subnet.gs_subnet_database.id
}
output "database_subnet_id_2" {
  value = aws_subnet.gs_subnet_database_2.id
}
output "bastion_subnet_id" {
  value = aws_subnet.gs_subnet_public_bastion.id
}
output "gs_vpc_id" {
  value = aws_vpc.gs_vpc.id
}