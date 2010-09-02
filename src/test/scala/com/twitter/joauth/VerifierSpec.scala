// Copyright 2010 Twitter, Inc.
// 
// Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
// file except in compliance with the License. You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

package com.twitter.joauth

import java.util.Date
import org.specs.mock.Mockito
import org.specs.Specification

class VerifierSpec extends Specification with Mockito {
  val checkNonce = mock[NonceValidator]
  val signer = mock[Signer]
  val request = mock[OAuth1Request]

  // 10 minutes ago
  val now = (new Date).getTime
  val longAgo = now - (10 * 60 * 1000)
  val farAhead = now + (10 * 60 * 1000)

  val verify = new StandardVerifier(signer, 5, checkNonce)

  "validateTimestamp" should {
    "return false for timestamp that is too old" in {
      verify.validateTimestamp(longAgo) must beFalse
    }
    "return true for timestamp that is new enough" in {
      verify.validateTimestamp((new Date).getTime) must beTrue
    }
    "return true for timestamp that too new" in {
      verify.validateTimestamp(farAhead) must beFalse
    }
  }
  "validateSignature" should {
    "return true for good signature" in {
      doReturn("foo").when(signer).apply("bar", "readsecret", "writesecret")
      doReturn("foo").when(request).signature
      doReturn("bar").when(request).normalizedRequest
      verify.validateSignature(request, "readsecret", "writesecret") must beTrue
    }
    "return false for bad signature" in {
      doReturn("foo").when(signer).apply("bar", "readsecret", "writesecret")
      doReturn("baz").when(request).signature
      doReturn("bar").when(request).normalizedRequest
      verify.validateSignature(request, "readsecret", "writesecret") must beFalse
    }
  }
  "Verifier" should {
    "return BAD_NONCE for bad nonce" in {
      doReturn("nonce").when(request).nonce
      doReturn(false).when(checkNonce).apply("nonce")
      verify(request, "readsecret", "writesecret") must be_==(VerifierResult.BAD_NONCE)
    }
    "return BAD_TIMESTAMP for bad timestamp" in {
      doReturn("nonce").when(request).nonce
      doReturn(longAgo).when(request).timestamp
      doReturn(true).when(checkNonce).apply("nonce")
      verify(request, "readsecret", "writesecret") must be_==(VerifierResult.BAD_TIMESTAMP)
    }
    "return BAD_SIGNATURE for bad signature" in {
      doReturn("foo").when(signer).apply("bar", "readsecret", "writesecret")
      doReturn("nonce").when(request).nonce
      doReturn("baz").when(request).signature
      doReturn("bar").when(request).normalizedRequest
      doReturn(now).when(request).timestamp
      doReturn(true).when(checkNonce).apply("nonce")
      verify(request, "readsecret", "writesecret") must be_==(VerifierResult.BAD_SIGNATURE)
    }
    "return OK for good request" in {
      doReturn("foo").when(signer).apply("bar", "readsecret", "writesecret")
      doReturn("nonce").when(request).nonce
      doReturn("foo").when(request).signature
      doReturn("bar").when(request).normalizedRequest
      doReturn(now).when(request).timestamp
      doReturn(true).when(checkNonce).apply("nonce")
      verify(request, "readsecret", "writesecret") must be_==(VerifierResult.OK)
    }
  }
}