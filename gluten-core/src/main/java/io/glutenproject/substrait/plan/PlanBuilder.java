/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.glutenproject.substrait.plan;

import io.glutenproject.substrait.SubstraitContext;
import io.glutenproject.substrait.extensions.AdvancedExtensionNode;
import io.glutenproject.substrait.extensions.ExtensionBuilder;
import io.glutenproject.substrait.extensions.FunctionMappingNode;
import io.glutenproject.substrait.rel.RelNode;
import io.glutenproject.substrait.type.TypeNode;

import java.util.ArrayList;
import java.util.Map;

public class PlanBuilder {
  private PlanBuilder() {}

  public static PlanNode makePlan(
      ArrayList<FunctionMappingNode> mappingNodes,
      ArrayList<RelNode> relNodes,
      ArrayList<String> outNames) {
    return new PlanNode(mappingNodes, relNodes, outNames);
  }

  public static PlanNode makePlan(
      ArrayList<FunctionMappingNode> mappingNodes,
      ArrayList<RelNode> relNodes,
      ArrayList<String> outNames,
      TypeNode outputSchema,
      AdvancedExtensionNode extension) {
    return new PlanNode(mappingNodes, relNodes, outNames, outputSchema, extension);
  }

  public static PlanNode makePlan(AdvancedExtensionNode extension) {
    return new PlanNode(extension);
  }

  public static PlanNode makePlan(
      SubstraitContext subCtx, ArrayList<RelNode> relNodes, ArrayList<String> outNames) {
    return makePlan(subCtx, relNodes, outNames, null, null);
  }

  public static PlanNode makePlan(
      SubstraitContext subCtx,
      ArrayList<RelNode> relNodes,
      ArrayList<String> outNames,
      TypeNode outputSchema,
      AdvancedExtensionNode extension) {
    if (subCtx == null) {
      throw new NullPointerException("ColumnarWholestageTransformer cannot doTansform.");
    }
    ArrayList<FunctionMappingNode> mappingNodes = new ArrayList<>();

    for (Map.Entry<String, Long> entry : subCtx.registeredFunction().entrySet()) {
      FunctionMappingNode mappingNode =
          ExtensionBuilder.makeFunctionMapping(entry.getKey(), entry.getValue());
      mappingNodes.add(mappingNode);
    }
    if (extension != null || outputSchema != null) {
      return makePlan(mappingNodes, relNodes, outNames, outputSchema, extension);
    }
    return makePlan(mappingNodes, relNodes, outNames);
  }

  public static PlanNode makePlan(SubstraitContext subCtx, ArrayList<RelNode> relNodes) {
    return makePlan(subCtx, relNodes, new ArrayList<>());
  }

  public static PlanNode empty() {
    return makePlan(new SubstraitContext(), new ArrayList<>(), new ArrayList<>());
  }
}
