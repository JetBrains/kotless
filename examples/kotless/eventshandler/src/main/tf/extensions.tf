resource "aws_dynamodb_table" "short_url_table" {
  name = "short-url-table"
  billing_mode = "PROVISIONED"
  read_capacity = 20
  write_capacity = 20
  hash_key = "URLHash"

  attribute {
    name = "URLHash"
    type = "S"
  }
}

resource "aws_s3_bucket" "bucket" {
  bucket = "kotless-test-bucket"
  acl = "private"
}

resource "aws_sqs_queue" "queue" {
  name = "kotless-test-queue"
  visibility_timeout_seconds = 300
}
