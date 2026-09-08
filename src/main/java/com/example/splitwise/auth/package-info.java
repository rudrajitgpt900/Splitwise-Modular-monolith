/**
 * Authentication and current-user access boundary.
 *
 * This module will translate the configured identity provider into the authenticated user
 * used by group authorization and will never accept a caller identity from request payloads.
 */
package com.example.splitwise.auth;