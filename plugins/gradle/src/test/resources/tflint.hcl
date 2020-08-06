config {
  module = false
  deep_check = false
  force = false
}

rule "terraform_unused_declarations" {
  enabled = true
}

rule "terraform_deprecated_index" {
  enabled = true
}

rule "terraform_naming_convention" {
  enabled = true
  custom = "^[a-z0-9]+([_-][a-z0-9]+)*$"
}
