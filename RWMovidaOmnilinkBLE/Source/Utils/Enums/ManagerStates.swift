//
//  ManagerStates.swift
//  PocMovidaBLE
//
//  Created by Joao Felipe on 5/24/17.
//  Copyright © 2017 João Felipe. All rights reserved.
//

enum EnumManagerCommandState: Int {
    case kInit = 1
    case kDeInit
    case kLock
    case kUnlock
    case kSeal
    case kUnseal
}

enum EnumManagerConnectionState: Int {
    case kDisconnected = 1
    case kConnected
}
