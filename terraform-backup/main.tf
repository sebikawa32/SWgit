########################################
# 0.  공통 설정
########################################
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# 기본 리전: ap‑northeast‑2 (서울)
provider "aws" {
  region = "ap-northeast-2"
}

# CloudFront용 ACM 인증서는 us‑east‑1 에 있어야 함 → 별도 provider
provider "aws" {
  alias  = "us_east_1"
  region = "us-east-1"
}

########################################
# 1.  S3 정적 웹사이트 버킷
########################################
resource "aws_s3_bucket" "frontend" {
  bucket = "jojinse-bucket"
  acl    = "public-read"

  website {
    index_document = "index.html"
    error_document = "index.html"
  }
}

resource "aws_s3_bucket_policy" "public_read" {
  bucket = aws_s3_bucket.frontend.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.frontend.arn}/*"
      }
    ]
  })
}

########################################
# 2.  CloudFront 배포 + 커스텀 도메인
########################################
data "aws_acm_certificate" "custom_cert" {
  provider    = aws.us_east_1
  arn         = "arn:aws:acm:us-east-1:771394549231:certificate/7e2c8092-7379-4321-b266-9135c21747b2"
}

resource "aws_cloudfront_distribution" "cdn" {
  enabled             = true
  default_root_object = "index.html"

  aliases = ["cute.sebinyeojins.com"]

  origins {
    domain_name = aws_s3_bucket.frontend.bucket_regional_domain_name
    origin_id   = "s3-frontend"
    s3_origin_config {
      origin_access_identity = ""   # 퍼블릭 버킷이라 OAI 생략
    }
  }

  default_cache_behavior {
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    viewer_protocol_policy = "redirect-to-https"

    target_origin_id = "s3-frontend"
  }

  price_class = "PriceClass_200"

  viewer_certificate {
    acm_certificate_arn            = data.aws_acm_certificate.custom_cert.arn
    ssl_support_method             = "sni-only"
    minimum_protocol_version       = "TLSv1.2_2021"
  }
}

########################################
# 3.  (참고) Secrets Manager – 이미 존재하므로 data 블록으로 참조만
########################################
data "aws_secretsmanager_secret" "db_secret" {
  name = "my-db-secert"
}

data "aws_secretsmanager_secret" "google_oauth" {
  arn = "arn:aws:secretsmanager:ap-northeast-2:771394549231:secret:ticketplanet/google-oauth-PspjTr"
}

data "aws_secretsmanager_secret" "misc_credentials" {
  arn = "arn:aws:secretsmanager:ap-northeast-2:771394549231:secret:ticketplanet/credentials-15Arti"
}

########################################
# 4.  (선택) 예시 EC2 4개 – 타입원하면 수정
########################################
resource "aws_instance" "app" {
  count         = 4
  ami           = "ami-0ff21ddca3f171465"   # 예시(ubuntu 22.04) – 실제로는 최신 AMI 검색해 교체
  instance_type = "t3.micro"
  key_name      = "my-keypair"              # 실제 키페어 이름으로 변경

  tags = {
    Name = "ticketplanet-${count.index}"
  }
}

########################################
# 5.  출력
########################################
output "s3_website_url" {
  value = aws_s3_bucket.frontend.website_endpoint
}

output "cloudfront_domain" {
  value = aws_cloudfront_distribution.cdn.domain_name
}

output "secret_db_arn" {
  value = data.aws_secretsmanager_secret.db_secret.arn
}

