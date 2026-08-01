package com.research.apca.algorithm.optimize.common.base;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Solution {
    private List<Individual> bestIndividualList =new ArrayList<>();
}
