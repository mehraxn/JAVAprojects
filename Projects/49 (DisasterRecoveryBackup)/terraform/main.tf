terraform {
  required_version = ">= 1.6.0, < 2.0.0"
}

resource "terraform_data" "recovery_design" {
  input = {
    status = "design-only"
  }
}

# TODO: Model approved disposable storage only after cost and deletion review.
