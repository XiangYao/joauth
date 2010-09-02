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

/**
 * If Unpacker encounters an unexpected exception, it will wrap it in an UnpackerException
 */
class UnpackerException(val message: String, t: Throwable) extends Exception(message, t) {
  def this(message: String) = this(message, null)
}

/**
 * thrown if it's unclear what auth type is being attempted
 */
class UnknownAuthType(message: String) extends UnpackerException(message)

/**
 * thrown if intent is clear, but the request is malformed
 */
class MalformedRequest(message: String) extends UnpackerException(message)